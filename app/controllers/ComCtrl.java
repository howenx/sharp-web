package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import util.Crypto;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static play.libs.Json.toJson;

/**
 * 公共
 * Created by sibyl.sun on 16/3/18.
 */
public class ComCtrl extends Controller {

    @Inject
    WSClient ws;

    @Inject
    private MemcachedClient cache;

    /**
     * 图片json串转图片url
     *
     * @param imgJson
     * @return
     */
    public String getImgUrl(String imgJson) {
        if (imgJson.contains("url")) {
            JsonNode jsonNode = Json.parse(imgJson);
            if (jsonNode.has("url")) {
                return jsonNode.get("url").asText();
            }
        }
        return SysParCom.IMAGE_URL + imgJson;
    }

    /**
     * 获取商品详情的URL
     *
     * @param oldUrl
     * @return
     */
    public String getDetailUrl(String oldUrl) {
//        Logger.info(oldUrl+"===="+oldUrl.indexOf("/detail/")+"==="+oldUrl.substring(oldUrl.indexOf("/detail/")));
        if (!oldUrl.contains("/detail/")) {
            return oldUrl;
        }
        return oldUrl.substring(oldUrl.indexOf("/detail/"));
//        Logger.info(oldUrl+"======="+skuType+"==="+PIN_PAGE);
//        if("pin".equals(skuType)){
//            return oldUrl.replace(PIN_PAGE,"");
//        }
//        if("item".equals(skuType)){
//            return oldUrl.replace(ITEM_PAGE,"");
//        }
//        if("vary".equals(skuType)){
//            return oldUrl.replace(VARY_PAGE,"");
//        }
//        if("customize".equals(skuType)){
//            return oldUrl.replace(CUSTOMIZE_PAGE,"");
//        }
//       return oldUrl;
    }

    public F.Promise<Result> getReqReturnMsg(String url) {
        return sendReq(url, null);

    }

    public F.Promise<Result> postReqReturnMsg(String url, RequestBody requestBody) {
        return sendReq(url, requestBody);

    }

    private F.Promise<Result> sendReq(String url, RequestBody requestBody) {
        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = null;
            if (null == requestBody) {
                request = builder.url(url).get().build();
            } else {
                request = builder.url(url).post(requestBody).build();
            }
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
            //   Logger.info(url+"返回结果---->\n"+json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误json=" + json);
                return badRequest();
            }
            return ok(toJson(message));
        });
    }

    /**
     * 订单加密
     *
     * @param orderId
     * @param token
     * @return
     */
    public String orderSecurityCode(String orderId, String token) {
        Map<String, String> map = new TreeMap<>();
        map.put("orderId", orderId);
        map.put("token", token);
        try {
            return Crypto.getSignature(map, "HMM");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 微信授权
     *
     * @param code  code
     * @param state state
     * @return Result
     */
    public F.Promise<Result> wechatUserinfo(String code, String state) {

        return ws.url(SysParCom.WEIXIN_ACCESS + "appid=" + WEIXIN_APPID + "&secret=" + WEIXIN_SECRET + "&code=" + code + "&grant_type=authorization_code").get().map(wsResponse -> {
            JsonNode response = wsResponse.asJson();

            Logger.info("微信scope userinfo返回的数据JSON: " + response.toString());

            if (response.findValue("errcode") == null && response.findValue("refresh_token") != null) {
                F.Promise<Result> t = ws.url(SysParCom.WEIXIN_REFRESH + "appid=" + WEIXIN_APPID + "&grant_type=refresh_token&refresh_token=" + response.findValue("refresh_token").asText()).get().map(wsr -> {
                    JsonNode refreshToken = wsr.asJson();
                    response().setCookie("accessToken", refreshToken.findValue("access_token").asText(), refreshToken.findValue("expires_in").asInt());
                    response().setCookie("orBind", "1", refreshToken.findValue("expires_in").asInt());
                    cache.set(refreshToken.findValue("access_token").asText(), refreshToken.findValue("expires_in").asInt(), refreshToken.findValue("openid").asText());
                    return redirect("/bind?state=" + state);
                });
                return t.get(1500);
            }
            return badRequest(views.html.error500.render());
        });
    }

    /**
     * 校验微信用户是否注册
     *
     * @param code  code
     * @param state state
     * @return Result
     */
    public F.Promise<Result> wechatBase(String code, String state) {
        return ws.url(SysParCom.WEIXIN_ACCESS + "appid=" + WEIXIN_APPID + "&secret=" + WEIXIN_SECRET + "&code=" + code + "&grant_type=authorization_code").get().map(wsResponse -> {
            JsonNode response = wsResponse.asJson();

            Logger.info("微信scope base返回的数据JSON: " + response.toString());

            F.Promise<Result> t = ws.url(WEIXIN_VERIFY + response.findValue("openid").asText()).get().map(wr -> {
                JsonNode json = wr.asJson();
                Message message = Json.fromJson(json.get("message"), Message.class);
                if (null == message) {
                    Logger.error("返回数据错误code=" + json);
                    return badRequest(views.html.error500.render());
                }
                if (message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                    Logger.error("返回数据code=" + json);
                    //此openId不存在时发起授权请求
                    return redirect(SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/userinfo", "utf-8") + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect");
                } else {
                    //刷新accessToken
                    F.Promise<Result> refresh = ws.url(SysParCom.WEIXIN_REFRESH + "appid=" + WEIXIN_APPID + "&grant_type=refresh_token&refresh_token=" + response.findValue("refresh_token").asText()).get().map(wsr -> {
                        JsonNode refreshToken = wsr.asJson();
                        Logger.error("刷新token请求返回结果------------->"+refreshToken.toString());
                        response().setCookie("accessToken", refreshToken.findValue("access_token").asText(), refreshToken.findValue("expires_in").asInt());
                        cache.set(refreshToken.findValue("access_token").asText(), refreshToken.findValue("expires_in").asInt(), refreshToken.findValue("openid").asText());

                        Logger.error("微信刷新token有效期----->" + refreshToken.findValue("expires_in").asInt());

                        //此openId存在则自动登录
                        String token = json.findValue("result").findValue("token").asText();
                        Integer expired = json.findValue("result").findValue("expired").asInt();
                        String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                        cache.set(session_id, expired, token);
                        response().setCookie("session_id", session_id, expired);
                        response().setCookie("user_token", token, expired);

                        Logger.error("Cache刷新token有效期----->" + expired);

                        Object uri = cache.get(state);
                        return redirect(uri == null ? "/" : uri.toString());
                    });
                    return refresh.get(1500);
                }
            });
            return t.get(1500);
        });
    }

    /**
     * 未登录或者登录两种情况下
     *
     * @param ctx
     * @return
     */
    public Request.Builder getBuilder(Http.Context ctx) {

        Request.Builder builder = new Request.Builder();
        builder.addHeader(Http.HeaderNames.X_FORWARDED_FOR, ctx.request().remoteAddress());
        builder.addHeader(Http.HeaderNames.VIA, ctx.request().remoteAddress());
        builder.addHeader("User-Agent", ctx.request().getHeader("User-Agent"));

        Optional<Http.Cookie> user_token = Optional.ofNullable(ctx.request().cookies().get("user_token"));
        Optional<Http.Cookie> session_id = Optional.ofNullable(ctx.request().cookies().get("session_id"));
        if (user_token.isPresent() && session_id.isPresent()) {
            builder.addHeader("id-token", user_token.get().value());
        }
        return builder;
    }

    /***
     * 获取token
     *
     * @param ctx
     * @return
     */
    public String getUserToken(Http.Context ctx) {
        Optional<Http.Cookie> user_token = Optional.ofNullable(ctx.request().cookies().get("user_token"));
        if (user_token.isPresent()) {
            return user_token.get().value();
        }
        return "";
    }

    /***
     * 未登录状态下返回的Message
     *
     * @param url
     * @return
     */
    public F.Promise<Result> getNotLoginMsg(String url) {
        ObjectNode result = Json.newObject();
        result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.USER_NOT_LOGIN.getIndex()), Message.ErrorCode.USER_NOT_LOGIN.getIndex())));
        String state = UUID.randomUUID().toString().replaceAll("-", "");
        cache.set(state, 60 * 60, url);
        result.put("state", state);
        return F.Promise.promise((F.Function0<Result>) () -> ok(result));
    }

    /***
     * 未登录状态下返回的Message
     *
     * @param url
     * @return
     */
    public F.Promise<Result> getNotLoginView(String url) {
        String state = UUID.randomUUID().toString().replaceAll("-", "");
        cache.set(state, 60 * 60, url);
        return F.Promise.promise(() -> ok(views.html.users.login.render(IMAGE_CODE, url, "?state=" + state)));
    }

    /**
     * 是否登录了
     *
     * @param ctx
     * @return
     */
    public boolean isHaveLogin(Http.Context ctx) {
        //普通浏览器
        Optional<Http.Cookie> user_token = Optional.ofNullable(ctx().request().cookies().get("user_token"));
        Optional<Http.Cookie> session_id = Optional.ofNullable(ctx().request().cookies().get("session_id"));
        if (user_token.isPresent() && null != user_token.get() && session_id.isPresent() && null != session_id.get()) {
            return true;
        }
        return false;

    }

    /**
     * 添加当前url的Cookie,用于userAjaxAuth注解登录成功后跳转
     *
     * @param ctx
     */
    public void addCurUrlCookie(Http.Context ctx) {
        //if (!isHaveLogin(ctx())) { //未登录情况下放入
        ctx.response().setCookie("curUrl", ctx().request().uri(), 60 * 60);
        //}
    }

    /**
     * 获取cookie的UUID
     * @param ctx
     * @return
     */

    public String getCookieUUID(Http.Context ctx){
        String cookieUUID="";
        Http.Cookie uuidCookie = ctx.request().cookie("uuid");
        if (null != uuidCookie) {
            cookieUUID=uuidCookie.value();
        }else{
            cookieUUID=UUID.randomUUID().toString().replaceAll("-", "");
            ctx.response().setCookie("uuid", cookieUUID, 60 * 60);
        }
        return cookieUUID;
    }

    /***
     * 只获取上一级目录
     * @param ctx
     * @return
     */
    public String getHistoryUrl(Http.Context ctx){
        String key=cacheHistoryKey(getCookieUUID(ctx));
        Stack<String> stack = (Stack<String>) cache.get(key);
        if(null!=stack){
            return stack.peek();
        }
        return "/";
    }

    /**
     * 记录访问页面顺序,如果是最后一条则pop,否则push,返回回退URL
     * @param ctx
     */
    public String pushOrPopHistoryUrl(Http.Context ctx){
        //用栈记录上一次访问的页面
        String url=ctx.request().uri();
        String key=cacheHistoryKey(getCookieUUID(ctx));
        Stack<String> stack = (Stack<String>) cache.get(key);
        if(null!=stack){
            if(url.equals(stack.peek())){
                stack.pop();//是上一次访问记录
                if(stack.empty()){
                    return "/";
                }
                return stack.peek();
            }
        }else{
            stack=new Stack<String>();
        }
        String hisUrl="/";
        if(!stack.empty()){
            hisUrl=stack.peek();
        }
        stack.push(url);
     //   Logger.info("====push==url==="+url);
        cache.set(key,60*60,stack);
        return hisUrl;
    }

    private String cacheHistoryKey(String cookieUUID){
        return cookieUUID+"_his";
    }

}
