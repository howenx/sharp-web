package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
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
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;


/**
 * 用户相关
 * Created by howen on 16/3/9.
 */
public class UserCtrl extends Controller {

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

    public Result setting() {
        return ok(views.html.users.setting.render());
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
