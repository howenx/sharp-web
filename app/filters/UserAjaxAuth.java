package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import domain.Message;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.UUID;

import static modules.SysParCom.*;

/**
 * 用户校验 ajax 请求未登录返回的是JSON
 * Created by howen on 15/11/25.
 */
public class UserAjaxAuth extends Security.Authenticator {

    @Inject
    private MemcachedClient cache;

    @Inject
    private WSClient ws;

    private Request.Builder builder = new Request.Builder();

    private String result;


    @Override
    public String getUsername(Http.Context ctx) {
        try {

            builder.addHeader(Http.HeaderNames.X_FORWARDED_FOR, ctx.request().remoteAddress());
            builder.addHeader(Http.HeaderNames.VIA, ctx.request().remoteAddress());
            builder.addHeader("User-Agent", ctx.request().getHeader("User-Agent"));

            Optional<Http.Cookie> user_token = Optional.ofNullable(ctx.request().cookies().get("user_token"));
            Optional<Http.Cookie> session_id = Optional.ofNullable(ctx.request().cookies().get("session_id"));
            if (user_token.isPresent() && session_id.isPresent()) {
                Optional<Object> cache_session_id = Optional.ofNullable(cache.get(session_id.get().value()));
                if (cache_session_id.isPresent() && user_token.get().value().equals(cache_session_id.get().toString())) {
                    Optional<String> token = Optional.ofNullable(cache.get(user_token.get().value()).toString());
                    if (token.isPresent()) {
                        String session_id_new = UUID.randomUUID().toString().replaceAll("-", "");
                        cache.delete(session_id.get().value());
                        cache.set(session_id_new, 7 * 24 * 60 * 60, cache_session_id.get());

                        ctx.response().discardCookie("session_id");
                        ctx.response().setCookie("session_id", session_id_new, 7 * 24 * 60 * 60);

                        JsonNode userJson = Json.parse(token.get());
                        Long userId = userJson.findValue("id").asLong();

                        ctx.args.put("request", builder.addHeader("id-token", user_token.get().value()));
                        return userId.toString();
                    } else return null;
                } else return null;
            } else {
                return weixin(ctx);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.info("userAuth:" + ex.getMessage());
            return null;
        }
    }


    private String weixin(Http.Context ctx) throws UnsupportedEncodingException {

        if (ctx.request().getHeader("User-Agent").contains("MicroMessenger")) {

            Logger.error("微信用户");

            Optional<Http.Cookie> accessToken = Optional.ofNullable(ctx.request().cookie("accessToken"));
            Optional<Http.Cookie> orBind = Optional.ofNullable(ctx.request().cookie("orBind"));

            if (accessToken.isPresent()) {

                Logger.error("accessToken----------->"+accessToken.get().value());

                Optional<Object> openid = Optional.ofNullable(cache.get(accessToken.get().value()));
                if (openid.isPresent()) {
                    Logger.error("openid----------->"+openid.get());

                    F.Promise<Result> t = ws.url(WEIXIN_VERIFY + openid.get().toString()).get().map(wr -> {

                        JsonNode json = wr.asJson();
                        Message message = Json.fromJson(json.get("message"), Message.class);
                        if (null == message) {
                            Logger.error("返回数据错误code=" + json);
                            result = null;
                        } else {
                            if (message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                                Logger.error("返回数据code=" + json);

                                //如果已经授权过则直接跳转到绑定页面
                                if (orBind.isPresent() && orBind.get().value().equals("1")) {
                                    result = "bind_page";
                                } else {
                                    //此openId不存在时发起授权请求
                                    result = "state_userinfo";
                                }
                            } else {
                                //此openId存在则自动登录
                                String token = json.findValue("result").findValue("token").asText();
                                Integer expired = json.findValue("result").findValue("expired").asInt();
                                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                                cache.set(session_id, expired, token);
                                ctx.response().setCookie("session_id", session_id, expired);
                                ctx.response().setCookie("user_token", token, expired);
                                ctx.args.put("request", builder.addHeader("id-token", token));
                                result = "success";
                            }
                        }
                        return null;
                    });
                    return t.get(1500) == null ? null : null;
                } else return null;
            } else {
                result = "state_base";
                return null;
            }
        } else {
            result = null;
            return null;
        }
    }


    @Override
    public Result onUnauthorized(Http.Context ctx) {



        String state = UUID.randomUUID().toString().replaceAll("-", "");

        String url="";
        if (ctx.request().method().equals("GET")) {
            Logger.error("上一次请求链接---->" + ctx.request().uri());
            cache.set(state, 60 * 60, ctx.request().uri());
        } else {
            Http.Cookie urlCookie=ctx.request().cookie("curUrl");
            if(null!=urlCookie){
             String  curUrl=urlCookie.value();
                if(null!=curUrl&&!"".equals(curUrl)){ //含有curUrl cookie
                    cache.set(state, 60 * 60, curUrl);
                }else{
                    cache.set(state, 60 * 60, "/");
                }
            }
            else{
                cache.set(state, 60 * 60, "/");
            }

        }

        Logger.error("这结果到底是啥?-------"+result);

     //   Logger.info("==UserAjaxAuth====="+cache.get(state).toString()+"==="+result);

        if (result != null && result.equals("state_base")) {
            try {
                url=SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/base", "UTF-8") + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                url="/login?state=" + state;
            }
        } else if (result != null && result.equals("bind_page")) {
            url="/bind?state=" + state;
        } else if (result != null && result.equals("state_userinfo")) {
            try {
                url=SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/userinfo", "UTF-8") + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                url="/login?state=" + state;
            }
        } else if (result != null && result.equals("success")) {
            String uri = cache.get(state).toString();
            if (uri == null) uri = "/";
            url=uri;
        } else {
            url = "/login?state=" + state;
        }

        //错误id是5006,message为登录跳转的url
        Message message=new Message(url,Message.ErrorCode.USER_NOT_LOGIN.getIndex());
        return ok(Json.toJson(message));
    }
}

