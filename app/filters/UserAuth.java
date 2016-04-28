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
 * 用户校验
 * Created by howen on 15/11/25.
 */
public class UserAuth extends Security.Authenticator {

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


            Optional<Http.Cookie> user_token = Optional.ofNullable(ctx.request().cookie("user_token"));
            Optional<Http.Cookie> session_id = Optional.ofNullable(ctx.request().cookie("session_id"));
            if (user_token.isPresent() && session_id.isPresent()) {

                Optional<Object> cache_session_id = Optional.ofNullable(cache.get(session_id.get().value()));


                if (cache_session_id.isPresent() && user_token.get().value().equals(cache_session_id.get().toString())) {

                    Optional<String> token = Optional.ofNullable(cache.get(user_token.get().value()).toString());
                    if (token.isPresent()) {
                        String session_id_new = UUID.randomUUID().toString().replaceAll("-", "");
                     //   cache.delete(session_id.get().value());
                        cache.set(session_id_new, 7 * 24 * 60 * 60, cache_session_id.get());

                        ctx.response().discardCookie("session_id");
                        ctx.response().setCookie("session_id", session_id_new, 7 * 24 * 60 * 60);

                        JsonNode userJson = Json.parse(token.get());
                        Long userId = userJson.findValue("id").asLong();

                        ctx.args.put("request", builder.addHeader("id-token", user_token.get().value()));
                        return userId.toString();
                    } else return null;
                } else return null;
            } else return weixin(ctx);


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
            Optional<Http.Cookie> wechat_refresh_token = Optional.ofNullable(ctx.request().cookie("wechat_refresh_token"));

            if (accessToken.isPresent() && wechat_refresh_token.isPresent()) {

                Optional<Object> openid = Optional.ofNullable(cache.get(accessToken.get().value()));
                Optional<Object> refreshToken_openId = Optional.ofNullable(cache.get(wechat_refresh_token.get().value()));

                if (openid.isPresent()) {
                    return getVerify(false, openid.get().toString(), orBind.isPresent() ? orBind.get().value() : null, ctx, null);
                } else if (refreshToken_openId.isPresent()) {
                    return getVerify(true, refreshToken_openId.get().toString(), orBind.isPresent() ? orBind.get().value() : null, ctx, wechat_refresh_token.get().value());
                } else {
                    result = "state_userinfo";
                    return null;
                }
            } else {
                result = "state_userinfo";
                return null;
            }
        } else {
            result = null;
            return null;
        }
    }


    private String getVerify(Boolean orAccess, String openid, String orBind, Http.Context ctx, String refresh) {

        F.Promise<Result> t = ws.url(WEIXIN_VERIFY + openid).get().map(wr -> {
            JsonNode json = wr.asJson();
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误code=" + json);
                result = null;
                return null;
            } else if (message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("非成功状态返回数据code=" + json);

                //如果已经授权过则直接跳转到绑定页面
                if (orBind != null && orBind.equals("1")) {
                    Logger.error("已经授权用户");
                    result = "bind_page";
                } else {
                    Logger.error("发起授权");
                    //此openId不存在时发起授权请求
                    result = "state_userinfo";
                }
                return null;
            } else {
                //此openId存在则自动登录
                String token = json.findValue("result").findValue("token").asText();
                Integer expired = json.findValue("result").findValue("expired").asInt();
                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                cache.set(session_id, expired, token);
                ctx.response().setCookie("session_id", session_id, expired);
                ctx.response().setCookie("user_token", token, expired);
                ctx.args.put("request", builder.addHeader("id-token", token));

                if (orAccess) {
                    ws.url(SysParCom.WEIXIN_REFRESH + "appid=" + WEIXIN_APPID + "&grant_type=refresh_token&refresh_token=" + refresh).get().map(wsr -> {
                        JsonNode refreshToken = wsr.asJson();
                        Logger.error("窝里--->" + refreshToken.toString());

                        String access_token = refreshToken.findValue("access_token").asText();
                        Integer expires_in = refreshToken.findValue("expires_in").asInt();
                        String refresh_token_new = refreshToken.findValue("refresh_token").asText();

                        ctx.response().setCookie("accessToken", access_token, expires_in);
                        cache.set(access_token, expires_in, openid);

                        ctx.response().setCookie("wechat_refresh_token", refresh_token_new, expired);
                        cache.set(refresh_token_new, expired, openid);
                        return null;
                    });
                }

                return ok("autoLogin");
            }
        });
        return t.get(1500) == null ? null : "success";
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {

        String state = getState(ctx);

        Logger.error("Auth校验结果------->" + result);

        if (result != null && result.equals("bind_page")) {
            return redirect("/bind?state=" + state);
        } else if (result != null && result.equals("state_userinfo")) {
            try {
                return redirect(SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/userinfo", "UTF-8") + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect");
            } catch (UnsupportedEncodingException e) {
                return redirect("/login?state=" + state);
            }
        } else return redirect("/login?state=" + state);
    }

    public Request.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(Request.Builder builder) {
        this.builder = builder;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getState(Http.Context ctx) {
        String state = UUID.randomUUID().toString().replaceAll("-", "");

        if (ctx.request().method().equals("GET")) {
            Logger.error("上一次请求链接---->" + ctx.request().uri());
            cache.set(state, 60 * 60, ctx.request().uri());
        } else {
            Http.Cookie urlCookie = ctx.request().cookie("curUrl");
            if (null != urlCookie) {
                String curUrl = urlCookie.value();
                if (null != curUrl && !"".equals(curUrl)) { //含有curUrl cookie
                    cache.set(state, 60 * 60, curUrl);
                } else {
                    cache.set(state, 60 * 60, "/");
                }
            } else {
                cache.set(state, 60 * 60, "/");
            }
        }
        return state;
    }
}

