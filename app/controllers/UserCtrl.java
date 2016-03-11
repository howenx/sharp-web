package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.*;
import domain.Collect;
import domain.CollectDto;
import domain.IndexMap;
import domain.Message;
import play.Logger;
import play.libs.F;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;


/**
 * 用户相关
 * Created by howen on 16/3/9.
 */
public class UserCtrl extends Controller {

    //收货地址
    public F.Promise<Result> address() {
        Promise<List<Address> > promiseOfInt = Promise.promise(() -> {
            Request request = new Request.Builder()
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

    public Result setting() {
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


    public F.Promise<Result> loginSubmit() {
        Map<String,String[]> stringMap = request().body().asFormUrlEncoded();
        Map<String,String> map = new HashMap<>();
        stringMap.forEach((k, v) -> map.put(k,v[0]));

        Logger.error("这你妈的啥:\n"+map.toString());

        Promise<Message> promiseOfInt = Promise.promise(() -> {
            FormEncodingBuilder feb =new FormEncodingBuilder();
            map.put("code","-1");
            map.forEach(feb::add);
            RequestBody formBody = feb.build();

            Request request = new Request.Builder()
                    .header("User-Agent",request().getHeader("User-Agent"))
                    .url(LOGIN_PAGE)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                Message message  =Json.fromJson(json, Message.class);
                Logger.error("测试----->\n"+ message);
                return message;
            }else  throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<Message, Result>) pi -> {
            Logger.error("返回---->\n"+pi);
            return ok("PI value computed: " + pi);
            }
        );
    }

}
