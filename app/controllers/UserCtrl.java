package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import domain.UserLoginInfo;
import filters.UserAuth;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.api.libs.Codecs;
import play.cache.Cache;
import play.data.Form;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.Crypto;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.LOGIN_PAGE;
import static modules.SysParCom.client;
import static play.libs.Json.newObject;


/**
 * 用户相关
 * Created by howen on 16/3/9.
 */
public class UserCtrl extends Controller {

    @Inject
    private MemcachedClient memchache;


    //收货地址
    public Result address() {
        return ok(views.html.users.address.render());
    }

    //创建新的收货地址
    public Result addressnew() {
        return ok(views.html.users.addressnew.render());
    }

    //身份认证
    public Result carded() {
        return ok(views.html.users.carded.render());
    }

    //优惠券
    public Result coupon() {
        return ok(views.html.users.coupon.render());
    }

    public Result login() {
        return ok(views.html.users.login.render());
    }

    public Result means() {
        return ok(views.html.users.means.render());
    }

    public Result myView() {
        return ok(views.html.users.my.render());
    }

    public Result navBar() {
        return ok(views.html.users.nav.render());
    }

    public Result regist() {
        return ok(views.html.users.regist.render());
    }

    public Result resetPasswd() {
        return ok(views.html.users.resetPasswd.render());
    }

    public Result tickling() {
        return ok(views.html.users.tickling.render());
    }

    @Security.Authenticated(UserAuth.class)
    public Result setting() {
        Request.Builder builder =(Request.Builder)ctx().args.get("request");


        Logger.error("session token----> " + session().get("id-token"));
        Logger.error("Cache user----> " + memchache.get(session().get("id-token")));
        Logger.error(request().cookie("user_token").value());

        return ok(views.html.users.setting.render());
    }


    public Promise<Result> loginSubmit() {


        ObjectNode result = newObject();
        Form<UserLoginInfo> userForm = Form.form(UserLoginInfo.class).bindFromRequest();
        Map<String, String> userMap = userForm.data();

        if (userForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {


            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();

                Request request = new Request.Builder()
                        .header("User-Agent", request().getHeader("User-Agent"))
                        .url(LOGIN_PAGE)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));
                } else throw new IOException("Unexpected code " + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {

                        Message message = Json.fromJson(json.findValue("message"), Message.class);
                        if (message.getCode().equals(200)) {
                            if (userMap.get("auto").equals("true")) {
                                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                                Cache.set(session_id, json.findValue("token").asText(), json.findValue("expired").asInt());
                                session("session_id", session_id);
                                response().setCookie("session_id", session_id, json.findValue("expired").asInt());
                                response().setCookie("user_token", json.findValue("token").asText(), json.findValue("expired").asInt());
                            }
                            session("id-token", json.findValue("token").asText());
                        }
                        Logger.error("测试----->\n" + json.toString() + " " + message.toString());
                        return ok(Json.toJson(message));
                    }
            );
        }
    }
}
