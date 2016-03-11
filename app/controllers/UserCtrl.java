package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
<<<<<<< HEAD
import com.fasterxml.jackson.databind.ObjectMapper;
=======
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.FormEncodingBuilder;
>>>>>>> c55e7943d2968e83c63421c5461eed2ab5e748c3
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
<<<<<<< HEAD
import domain.*;
=======
import domain.Message;
import domain.UserLoginInfo;
import filters.UserAuth;
import net.spy.memcached.MemcachedClient;
>>>>>>> c55e7943d2968e83c63421c5461eed2ab5e748c3
import play.Logger;
import play.api.libs.Codecs;
import play.cache.Cache;
import play.data.Form;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.Crypto;

import javax.inject.Inject;
import java.io.IOException;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
>>>>>>> c55e7943d2968e83c63421c5461eed2ab5e748c3

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
    public F.Promise<Result> address() {
        Promise<List<Address> > promiseOfInt = Promise.promise(() -> {
            Http.Request request = new Request.Builder()
                    .header("User-Agent", request().getHeader("User-Agent")).addHeader("id-token","db95fc4fc315ccb739f26fb4d0d8783c")
                    .url(ADDRESS_PAGE)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                Logger.info("===json==" + json);
                Message message = Json.fromJson(json.get("message"), Message.class);
                //TODO ... 失败
                ObjectMapper mapper = new ObjectMapper();
                List<Address> addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {});
                return addressList;
            }else  throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<List<Address> , Result>) pi -> {
                    Logger.error("返回---->\n"+pi);
                    return ok(views.html.users.address.render(pi));
                }
        );
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

    /**
     * 我的收藏
     * @return
     */
    public F.Promise<Result> collect() {
        Promise<List<CollectDto> > promiseOfInt = Promise.promise(() -> {
            Request request = new Request.Builder()
                    .header("User-Agent", request().getHeader("User-Agent")).addHeader("id-token","db95fc4fc315ccb739f26fb4d0d8783c")
                    .url(COLLECT_PAGE)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                Logger.info("===json==" + json);
                Message message = Json.fromJson(json.get("message"), Message.class);
                ObjectMapper mapper = new ObjectMapper();
                List<CollectDto> collectList = mapper.readValue(json.get("collectList").toString(), new TypeReference<List<CollectDto>>() {});
                return collectList;
            }else  throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<List<CollectDto> , Result>) pi -> {
                    Logger.error("返回---->\n"+pi);
                    return ok(views.html.users.collect.render(pi));
                }
        );
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
