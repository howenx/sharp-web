package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import domain.Message;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.cache.Cache;
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

    private String uri;

    @Override
    public String getUsername(Http.Context ctx) {
        try {
            Optional<String> header = Optional.ofNullable(ctx.session().get("id-token"));
            Request.Builder builder = new Request.Builder();
            builder.addHeader(Http.HeaderNames.X_FORWARDED_FOR, ctx.request().remoteAddress());
            builder.addHeader(Http.HeaderNames.VIA, ctx.request().remoteAddress());
            builder.addHeader("User-Agent", ctx.request().getHeader("User-Agent"));

            if (header.isPresent()) {
                Optional<String> token = Optional.ofNullable(cache.get(header.get()).toString());
                if (token.isPresent()) {
                    ctx.session().put("id-token", header.get());
                    JsonNode userJson = Json.parse(token.get());
                    Long userId = userJson.findValue("id").asLong();
                    ctx.args.put("request", builder.addHeader("id-token", header.get()));
                    return userId.toString();
                } else return null;

            } else {
                Optional<String> user_token = Optional.ofNullable(ctx.request().cookies().get("user_token").value());
                Optional<String> session_id = Optional.ofNullable(ctx.request().cookies().get("session_id").value());
                if (user_token.isPresent() && session_id.isPresent()) {
                    Optional<String> cache_session_id = Optional.ofNullable(Cache.get(session_id.get()).toString());
                    if (cache_session_id.isPresent() && cache_session_id.get().equals(user_token.get())) {
                        Optional<String> token = Optional.ofNullable(cache.get(user_token.get()).toString());
                        if (token.isPresent()) {
                            String session_id_new = UUID.randomUUID().toString().replaceAll("-", "");
                            Cache.remove(session_id.get());
                            Cache.set(session_id_new, token.get(), 7 * 24 * 60 * 60);
                            ctx.response().discardCookie("session_id");
                            ctx.response().setCookie("session_id", session_id_new, 7 * 24 * 60 * 60);

                            JsonNode userJson = Json.parse(token.get());
                            Long userId = userJson.findValue("id").asLong();
                            ctx.session().put("id-token", token.get());
                            ctx.args.put("request", builder.addHeader("id-token", token.get()));
                            return userId.toString();
                        } else return null;
                    } else return null;
                } else return weixin(ctx);
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
            Logger.info("userAuth:" + ex.getMessage());
            return null;
        }
    }


    private String weixin(Http.Context ctx) throws UnsupportedEncodingException {

        Logger.error("微信用户");
        String state = UUID.randomUUID().toString().replaceAll("-", "");
        cache.set(state, 60 * 60, ctx.request().uri());

        if (ctx.request().getHeader("User-Agent").contains("MicroMessenger")) {
            if (ctx.session().get("openId")!=null && ctx.session().get("accessToken")!=null){
                return ws.url(WEIXIN_VERIFY+ ctx.session().get("openid")).get().map(wr->{
                    JsonNode json  = wr.asJson();
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if (null == message) {
                        Logger.error("返回数据错误code=" + json);
                        return badRequest(views.html.error500.render());
                    }
                    if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                        Logger.error("返回数据code=" + json);
                        //此openId不存在时发起授权请求
                        this.uri = state;
                        return "state_userinfo";
                    }

                    //此openId存在则自动登录
                    String token = json.findValue("result").findValue("token").asText();
                    Integer expired = json.findValue("result").findValue("expired").asInt();
                    String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                    Cache.set(session_id, token, expired);
                    ctx.session().put("session_id", session_id);
                    ctx.response().setCookie("session_id", session_id, expired);
                    ctx.response().setCookie("user_token", token, expired);
                    ctx.session().put("id-token", token);
                    this.uri = state;
                    return "success";
                }).get(10).toString();
            }else return "state_base";
        }else return null;
    }


    @Override
    public Result onUnauthorized(Http.Context ctx) {
        String state = UUID.randomUUID().toString().replaceAll("-", "");
        cache.set(state, 60 * 60, ctx.request().uri());
        uri = state;
        if (getUsername(ctx)!=null && getUsername(ctx).equals("state_base")){
            try {
                return  redirect(SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/base", "UTF-8") + "&response_type=code&scope=snsapi_base&state=" + cache.get(uri).toString() + "#wechat_redirect");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return redirect("/login?state="+uri);
            }
        }else if (getUsername(ctx)!=null && getUsername(ctx).equals("state_userinfo")){
            try {
                return  redirect(SysParCom.WEIXIN_CODE_URL+"appid="+WEIXIN_APPID+"&&redirect_uri="+ URLEncoder.encode(M_HTTP+"/wechat/userinfo","UTF-8")+"&response_type=code&scope=snsapi_userinfo&state="+cache.get(uri).toString() +"#wechat_redirect");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return redirect("/login?state="+uri);
            }
        }else return redirect("/login?state="+uri);
    }
}

