package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import net.spy.memcached.MemcachedClient;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户校验
 * Created by howen on 15/11/25.
 */
public class UserAuth extends Security.Authenticator {

    @Inject
    private MemcachedClient cache;

    @Override
    public String getUsername(Http.Context ctx) {
        Optional<String> header = Optional.ofNullable(ctx.session().get("id-token"));
        if (header.isPresent()) {
            Optional<String> token = Optional.ofNullable(cache.get(header.get()).toString());
            if (token.isPresent()) {
                ctx.session().put("id-token",header.get());
                JsonNode userJson = Json.parse(token.get());
                Long userId = userJson.findValue("id").asLong();
                ctx.args.put("request",new Request.Builder().header("User-Agent", ctx.request().getHeader("User-Agent")).addHeader("id-token",header.get()));
                return userId.toString();
            }
            else return null;
        }else{
            Optional<String> user_token = Optional.ofNullable(ctx.request().cookie("user_token").value());
            Optional<String> session_id = Optional.ofNullable(ctx.request().cookie("session_id").value());
            if (user_token.isPresent() && session_id.isPresent() ){
                if (Cache.get(session_id.get())!=null && Cache.get(session_id.get()).equals(user_token.get())){
                    Optional<String> token = Optional.ofNullable(cache.get(user_token.get()).toString());
                    if (token.isPresent()) {
                        String session_id_new = UUID.randomUUID().toString().replaceAll("-", "");
                        Cache.remove(session_id.get());
                        Cache.set(session_id_new,user_token.get(), 7*24*60*60);
                        ctx.response().discardCookie("session_id");
                        ctx.response().setCookie("session_id", session_id_new, 7*24*60*60);

                        JsonNode userJson = Json.parse(token.get());
                        Long userId = userJson.findValue("id").asLong();
                        ctx.session().put("id-token",user_token.get());
                        ctx.args.put("request",new Request.Builder().header("User-Agent", ctx.request().getHeader("User-Agent")).addHeader("id-token",header.get()));
                        return userId.toString();
                    }else return null;
                } else return null;
            }else return null;
        }
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect("/login");
    }
}

