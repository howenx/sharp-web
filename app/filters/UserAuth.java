package filters;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Request;
import controllers.UserCtrl;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.api.data.OptionalMapping;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import scala.Array;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
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
        try {
            Optional<String> header = Optional.ofNullable(ctx.session().get("id-token"));
            Map<String, String> map =
                    UserCtrl.mapper.convertValue(scala.collection.JavaConverters.mapAsJavaMapConverter(ctx._requestHeader().headers().toSimpleMap()).asJava(), UserCtrl.mapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
            Request.Builder builder = new Request.Builder();
            map.forEach(builder::addHeader);

            if (header.isPresent()) {
                Optional<String> token = Optional.ofNullable(cache.get(header.get()).toString());
                if (token.isPresent()) {
                    ctx.session().put("id-token", header.get());
                    JsonNode userJson = Json.parse(token.get());
                    Long userId = userJson.findValue("id").asLong();
                    ctx.args.put("request", new Request.Builder().header("User-Agent", ctx.request().getHeader("User-Agent")).addHeader("id-token", header.get()));
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
                } else return null;
            }
        } catch (Exception ex) {
            Logger.info("userAuth:" + ex.getMessage());
            return null;
        }
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return redirect("/login");
    }
}

