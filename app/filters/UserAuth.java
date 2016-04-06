package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import domain.Message;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
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
                weixin(ctx);
                return result ;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.info("userAuth:" + ex.getMessage());
            return null;
        }
    }


    private void weixin(Http.Context ctx) throws UnsupportedEncodingException {

        if (ctx.request().getHeader("User-Agent").contains("MicroMessenger")) {

            Logger.error("微信用户");

            Optional<Http.Cookie> openId = Optional.ofNullable(ctx.request().cookie("openId"));
            Optional<Http.Cookie> accessToken = Optional.ofNullable(ctx.request().cookie("accessToken"));

            if (openId.isPresent() && accessToken.isPresent()) {
                ws.url(WEIXIN_VERIFY + openId.get().value()).get().map(wr -> {

                    Logger.error("擦擦");

                    JsonNode json = wr.asJson();
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if (null == message) {
                        Logger.error("返回数据错误code=" + json);
//                        return badRequest(views.html.error500.render());
                    }
                    if (message != null && message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                        Logger.error("返回数据code=" + json);
                        //此openId不存在时发起授权请求
                        result = "state_userinfo";
                    }

                    //此openId存在则自动登录
                    String token = json.findValue("result").findValue("token").asText();
                    Integer expired = json.findValue("result").findValue("expired").asInt();
                    String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                    cache.set(session_id, expired, token);
                    ctx.response().setCookie("session_id", session_id, expired);
                    ctx.response().setCookie("user_token", token, expired);
                    ctx.args.put("request", builder.addHeader("id-token", token));

                    result = "success";
                    return null;
                });
            } else result = "state_base";
        } else result = null;
    }


    @Override
    public Result onUnauthorized(Http.Context ctx) {

        String state = UUID.randomUUID().toString().replaceAll("-", "");

        if (ctx.request().method().equals("GET")) {
            cache.set(state, 60 * 60, ctx.request().uri());
        } else cache.set(state, 60 * 60, "/");

        if (result != null && result.equals("state_base")) {
            try {
                return redirect(SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/base", "UTF-8") + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect");
            } catch (UnsupportedEncodingException e) {
                return redirect("/login?state=" + state);
            }
        } else if (result != null && result.equals("state_userinfo")) {
            try {
                return redirect(SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/userinfo", "UTF-8") + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect");
            } catch (UnsupportedEncodingException e) {
                return redirect("/login?state=" + state);
            }
        } else if (result!= null && result.equals("success")) {
            String uri = cache.get(state).toString();
            if (uri == null) uri = "/";
            return redirect(uri);
        } else return redirect("/login?state=" + state);
    }
}

