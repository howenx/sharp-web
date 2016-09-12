package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import domain.Theme;
import domain.ThemeItem;
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
import java.text.SimpleDateFormat;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static play.libs.Json.toJson;

/**
 * 公共
 * Created by sibyl.sun on 16/3/18.
 */
@SuppressWarnings("unchecked")
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
        if ("" != imgJson && null != imgJson && imgJson.contains("url")) {
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

    /**
     * 处理TDPU类型的目标地址在M端应该转化成的最终地址  ,如首页slider
     * @param targetType T:主题，D:详细页面，P:拼购商品页，U:一个促销活动的链接（h5主题）
     * @param itemTarget 目标地址
     * @return
     */
    public String getFinalItemTargetByTypeTDPU(String targetType,String itemTarget){
        String url=itemTarget;
        if (url.contains(GOODS_PAGE)) {
            url=url.replace(GOODS_PAGE, "");
        }
        if (url.contains(THEME_PAGE) && Objects.equals(targetType, "T")) {
            url=url.replace(THEME_PAGE, "");
        }
        if (url.contains(NAV_PAGE) && Objects.equals(targetType, "M")) {
            url=url.replace(NAV_PAGE, "");
        }
        //处理成最终跳转地址
        if("D".equals(targetType)||"P".equals(targetType)){
            url="/detail/"+url;
        }
        if("U".equals(targetType)){
            if(!url.contains("jd.com")){ //非京东的
                url=url+"/M";
            }
        }
        if("T".equals(targetType)){
            url="/themeDetail/"+url;
        }
        if("M".equals(targetType)){
            url="/nav/"+url+"1/1";
        }

        return url;

    }

    /**
     * 处理主题成最终的地址
     * @param theme
     */
    public void handleThemeUrl(Theme theme){
        //按照类型处理跳转地址
        if ("ordinary".equals(theme.getType())) {
            String themeUrl = theme.getThemeUrl();
            themeUrl = themeUrl.replace(THEME_PAGE, "");
            theme.setThemeUrl("/themeDetail/" + themeUrl);
        }
        if ("detail".equals(theme.getType()) || "pin".equals(theme.getType())) {
            String themeUrl = theme.getThemeUrl();
            theme.setThemeUrl(getDetailUrl(themeUrl));
        }
        if ("h5".equals(theme.getType())) {
            if(!theme.getThemeUrl().contains("jd.com")){ //非京东的
                theme.setThemeUrl(theme.getThemeUrl() + "/M");
            }
        }
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

            Logger.error("微信通过code换取网页授权access_token返回的数据JSON: " + response.toString());

//            ws.url(SysParCom.WEIXIN_UNION + "access_token=" + response.findValue("access_token").asText() + "&openid=" + response.findValue("openid").asText() + "&lang=zh_CN").get().map(wsResponse1 -> {
//
//                JsonNode union = wsResponse1.asJson();
//                Logger.error("微信拉取用户信息(需scope为 snsapi_userinfo)返回结果:"+union.toString());
//                cache.set(response.findValue("openid").asText(), WEIXIN_REFRESH_OVERTIME, union.findValue("unionid").asText());
//                response().setCookie("unionId", union.findValue("unionid").asText(), WEIXIN_REFRESH_OVERTIME);
//                return null;
//            });

            if (response.findValue("errcode") == null && response.findValue("refresh_token") != null) {
                F.Promise<Result> t = ws.url(SysParCom.WEIXIN_REFRESH + "appid=" + WEIXIN_APPID + "&grant_type=refresh_token&refresh_token=" + response.findValue("refresh_token").asText()).get().map(wsr -> {
                    JsonNode refreshToken = wsr.asJson();
                    Logger.error("微信刷新access_token返回数据" + refreshToken.toString());
                    response().setCookie("accessToken", refreshToken.findValue("access_token").asText(), refreshToken.findValue("expires_in").asInt());
                    response().setCookie("orBind", "1", refreshToken.findValue("expires_in").asInt());
                    cache.set(refreshToken.findValue("access_token").asText(), refreshToken.findValue("expires_in").asInt(), refreshToken.findValue("openid").asText());
                    response().setCookie("wechat_refresh_token", refreshToken.findValue("refresh_token").asText(), WEIXIN_REFRESH_OVERTIME);
                    cache.set(refreshToken.findValue("refresh_token").asText(), WEIXIN_REFRESH_OVERTIME, refreshToken.findValue("openid").asText());

                    //union id
                    if (response.findValue("unionid")!=null){
                        cache.set(response.findValue("openid").asText(), WEIXIN_REFRESH_OVERTIME, response.findValue("unionid").asText());
                        response().setCookie("unionId", response.findValue("unionid").asText(), WEIXIN_REFRESH_OVERTIME);
                    }

                    Object uri = cache.get(state);
                    return redirect(uri == null ? "/" : uri.toString());
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

            String openId = response.findValue("openid").asText();

            String refresh_token = response.findValue("refresh_token").asText();


            F.Promise<Result> t = ws.url(WEIXIN_VERIFY +"?openId="+ openId+"&idType=W").get().map(wr -> {
                JsonNode json = wr.asJson();

                Logger.error("ID系统校验返回------->" + json.toString());

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

                    Logger.error("微信access获取到的refresh token------->" + refresh_token);

                    String idToken = json.findValue("result").findValue("token").asText();
                    Integer idExpired = json.findValue("result").findValue("expired").asInt();

                    F.Promise<Result> refresh = getRefresh(refresh_token, idToken, idExpired, state, true);

                    return refresh.get(1500);
                }
            });
            return t.get(1500);
        });
    }


    private F.Promise<Result> getRefresh(String refresh_token, String idToken, Integer idExpired, String state, Boolean orBind) {
        //刷新accessToken
        return ws.url(SysParCom.WEIXIN_REFRESH + "appid=" + WEIXIN_APPID + "&grant_type=refresh_token&refresh_token=" + refresh_token).get().map(wsr -> {
            JsonNode refreshToken = wsr.asJson();
            Logger.error("请求微信刷新token结果--->" + refreshToken.toString());

            String access_token = refreshToken.findValue("access_token").asText();
            Integer expires_in = refreshToken.findValue("expires_in").asInt();
            String openid = refreshToken.findValue("openid").asText();
            String refresh_token_new = refreshToken.findValue("refresh_token").asText();

            response().setCookie("accessToken", access_token, expires_in);
            cache.set(access_token, expires_in, openid);

            response().setCookie("wechat_refresh_token", refresh_token_new, idExpired);
            cache.set(refresh_token_new, idExpired, openid);

            if (orBind) {
                //此openId存在则自动登录
                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                cache.set(session_id, idExpired, idToken);

                response().setCookie("session_id", session_id, idExpired);
                response().setCookie("user_token", idToken, idExpired);

                Logger.error("Cache刷新token有效期----->" + idExpired);
                Logger.error("微信刷新token有效期----->" + expires_in);

            } else {
                response().setCookie("orBind", "1", expires_in);
            }

            Object uri = cache.get(state);
            return redirect(uri == null ? "/" : uri.toString());

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
        //   Optional<Http.Cookie> session_id = Optional.ofNullable(ctx.request().cookies().get("session_id"));
        if (user_token.isPresent() /*&& session_id.isPresent()*/) {
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
        //  Optional<Http.Cookie> session_id = Optional.ofNullable(ctx().request().cookies().get("session_id"));
        if (user_token.isPresent() && null != user_token.get()/* && session_id.isPresent() && null != session_id.get()*/) {
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
     *
     * @param ctx
     * @return
     */

    public String getCookieUUID(Http.Context ctx) {
        String cookieUUID = "";
        Http.Cookie uuidCookie = ctx.request().cookie("uuid");
        if (null != uuidCookie) {
            cookieUUID = uuidCookie.value();
        } else {
            cookieUUID = UUID.randomUUID().toString().replaceAll("-", "");
            ctx.response().setCookie("uuid", cookieUUID, 60 * 60);
        }
        return cookieUUID;
    }

    /**
     * 缓存历史记录的键值
     * @param cookieUUID
     * @return
     */
    private String cacheHistoryKey(String cookieUUID) {
        return cookieUUID + "_his";
    }

    /***
     * 只获取上一级目录
     *
     * @param ctx
     * @return
     */
    public String getHistoryUrl(Http.Context ctx) {
        String key = cacheHistoryKey(getCookieUUID(ctx));
        List<String> stack = (List<String>) cache.get(key);
        String hisUrl = "/";
        if (null != stack && stack.size() > 0) {
            hisUrl = stack.get(stack.size() - 1);
            if (hisUrl.equals(ctx.request().uri())) {
                if (stack.size() >= 2) {
                    hisUrl = stack.get(stack.size() - 2);
                } else {
                    hisUrl = "/";
                }
            }
        }
        return hisUrl;
    }

    /**
     * 记录访问页面顺序,如果是最后一条则pop,否则push,返回回退URL
     *
     * @param ctx
     */
    public String pushOrPopHistoryUrl(Http.Context ctx) {
        //用栈记录上一次访问的页面
        String url = ctx.request().uri();
        String key = cacheHistoryKey(getCookieUUID(ctx));
        List<String> list = (List<String>) cache.get(key);
        if ("/".equals(url) && null != list) { //首页清一下返回
            list.clear();
            cache.set(key, 60 * 60, list);
            return "/";
        }
        String hisUrl = "/";
        if (null != list && list.size() > 0) {
//            for(int i=list.size()-1;i>=0;i--){
//                Logger.info("===>"+list.get(i));
//
//            }
            // Logger.info(url+"===pushOrPopHistoryUrl===="+url.equals(list.get(list.size()-1))+"==list.peek=="+list.get(list.size()-1));
            if (url.equals(list.get(list.size() - 1)) || (list.size() >= 2 && url.equals(list.get(list.size() - 2)))) {
                //路线1:主题——>详情-->返回主题-->详情-->返回主题
                //路线2:主题——>详情-->购物车-->详情-->返回购物车-->返回
                //路线3:拼购路线返回
                //路线4:首页-->购物车-->详情-->返回购物车-->返回
                list.remove(list.size() - 1);//是上一次访问记录
                if (!list.isEmpty()) {
                    hisUrl = list.get(list.size() - 1);
                    if (hisUrl.equals(url)) {
                        if (list.size() >= 2) {
                            hisUrl = list.get(list.size() - 2);
                        } else {
                            hisUrl = "/";
                        }
                    }
                }
                cache.set(key, 60 * 60, list);
                //               Logger.info("====pop==url==="+url+"==hisUrl="+hisUrl);
                return hisUrl;
            }

        } else {
            list = new ArrayList<String>();
        }

        if (!list.isEmpty()) {
            hisUrl = list.get(list.size() - 1);
        }
        list.add(url);
        cache.set(key, 60 * 60, list);
        return hisUrl;
    }

    /**
     * 获取展示的身份证号码
     * @param idCardNum
     * @return
     */
    public static  String getShowIdCardNum(String idCardNum){
        return idCardNum.substring(0,10)+"****"+idCardNum.substring(14);
    }

    /**
     * 获取展示的手机号码
     * @param tel
     * @return
     */
    public static  String getShowTel(String tel){
        return tel.substring(0,3)+"****"+tel.substring(7);
    }

    /**
     * H5页面在跳转之前需要的操作
     * @param ctx
     */
    public void h5OpbeforeRender(Http.Context ctx){
        pushOrPopHistoryUrl(ctx());
        addCurUrlCookie(ctx());
    }

    /**
     * 解析themeItem
     * @param themeItem
     * @return
     */
    public ThemeItem parseThemeItem(ThemeItem themeItem){
        try {
            JsonNode itemImgJson = Json.parse(themeItem.getItemImg());
            themeItem.setItemImg(itemImgJson.get("url").asText());
            themeItem.setItemUrl(themeItem.getItemUrl().replace(GOODS_PAGE, ""));
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strNow = sdfDate.format(now);
            Date endAtDate = sdfDate.parse(themeItem.getEndAt());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endAtDate);
            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
                hour = "0" + hour;
            }
            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
            if (calendar.get(Calendar.MINUTE) < 10) {
                minute = "0" + minute;
            }
            String endDate = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + hour + ":" + minute;
            if (themeItem.getEndAt().compareTo(strNow) < 0 || themeItem.getState() == "D" || themeItem.getState() == "N" || themeItem.getState() == "K") {
                themeItem.setEndAt("已结束");
            } else {
                themeItem.setEndAt("截止" + endDate);
            }
            return themeItem;


        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 禁止浏览器缓存
     * @param ctx
     */
    public void disableBrowserCache(Http.Context ctx){
        ctx.response().setHeader("Cache-Control","no-cache,no-store,must-revalidate");
        ctx.response().setHeader("Expires", "0");
        ctx.response().setHeader("Pragma","no-cache");
    }

}
