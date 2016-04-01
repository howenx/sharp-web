package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import domain.WechatVo;
import filters.UserAuth;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.Crypto;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

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
        if (oldUrl.indexOf("/detail/") < 0) {
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

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> getReqReturnMsg(String url) {
        return sendReq(url, null);

    }

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> postReqReturnMsg(String url, RequestBody requestBody) {
        return sendReq(url, requestBody);

    }

    @Security.Authenticated(UserAuth.class)
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


    public F.Promise<Result> wechatUserinfo(String code, String state) {

        return ws.url(SysParCom.WEIXIN_ACCESS + "appid=" + WEIXIN_APPID + "&secret=" + WEIXIN_SECRET + "&code=" + code + "&grant_type=authorization_code").get().map(wsResponse -> {
            JsonNode response = wsResponse.asJson();

            Logger.info("微信scope userinfo返回的数据JSON: " + response.toString());

            if (response.findValue("errcode") == null && response.findValue("refresh_token") != null) {
                F.Promise<Result> t = ws.url(SysParCom.WEIXIN_REFRESH + "appid=" + WEIXIN_APPID + "&grant_type=refresh_token&refresh_token=" + response.findValue("refresh_token")).get().map(wsr -> {
                    JsonNode refreshToken = wsr.asJson();
                    cache.set(refreshToken.findValue("openid").asText(), refreshToken.findValue("expires_in").asInt(), new WechatVo(refreshToken.findValue("openid").asText(), refreshToken.findValue("access_token").asText()));
                    ctx().response().setCookie("openId", refreshToken.findValue("openid").asText(),refreshToken.findValue("expires_in").asInt());
                    ctx().response().setCookie("accessToken", refreshToken.findValue("access_token").asText(),refreshToken.findValue("expires_in").asInt());
                    return redirect("/bind?state=" + state);
                });
                return t.get(10);
            }
            return badRequest(views.html.error500.render());
        });
    }

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
                    redirect(SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/userinfo", "utf-8") + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect");
                }

                //此openId存在则自动登录
                String token = json.findValue("result").findValue("token").asText();
                Integer expired = json.findValue("result").findValue("expired").asInt();
                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                cache.set(session_id, expired, token);
                response().setCookie("session_id", session_id, expired);
                response().setCookie("user_token", token, expired);

                String uri = cache.get(state).toString();
                if (uri==null) uri="/";

                return redirect(uri);
            });
            return t.get(10);
        });

    }

//    public Request.Builder getBuilder(Http.Request request, Http.Session session) {
//        Request.Builder builder = new Request.Builder();
//        builder.addHeader(Http.HeaderNames.X_FORWARDED_FOR, request.remoteAddress());
//        builder.addHeader(Http.HeaderNames.VIA, request.remoteAddress());
//        builder.addHeader("User-Agent", request.getHeader("User-Agent"));
//        if (session.containsKey("id-token")) {
//            builder.addHeader("id-token", session.get("id-token"));
//        }
//        return builder;
//    }

    /**
     * 未登录或者登录两种情况下
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
}
