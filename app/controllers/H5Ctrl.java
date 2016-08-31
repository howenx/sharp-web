package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.Message;
import filters.UserAjaxAuth;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.LOG_OPEN;
import static modules.SysParCom.SHOPPING_COUPON_REC;
import static modules.SysParCom.client;
import static play.libs.Json.toJson;

/**
 * Created by sibyl.sun on 16/8/18.
 */
public class H5Ctrl extends Controller {

    @Inject
    ComCtrl comCtrl;
    //new
    public Result article(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.article.render(openType));
    }

    //gather  assemblage
    public Result gather(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.gather.render(openType));
    }

    //assemblage
    public Result assemblage(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.assemblage.render(openType));
    }

    //优惠券
    public Result hp1(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.hp1.render(openType,SHOPPING_COUPON_REC));
    }

    public Result hp2(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.hp2.render(openType,SHOPPING_COUPON_REC));
    }

    public Result hp3(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.hp3.render(openType));
    }

    public Result hp4(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.hp4.render(openType));
    }

     public Result hp5(String openType) {
            comCtrl.h5OpbeforeRender(ctx());
            return ok(views.html.shopping.h5.hp5.render(openType));
        }

    public Result hp6(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.hp6.render(openType));
    }
    public Result hp7(String openType) {
         comCtrl.h5OpbeforeRender(ctx());
         return ok(views.html.shopping.h5.hp7.render(openType));
    }
    public Result hp8(String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        return ok(views.html.shopping.h5.hp8.render(openType));
    }

    public Result hp9(String openType) {
            comCtrl.h5OpbeforeRender(ctx());
            return ok(views.html.shopping.h5.hp9.render(openType));
        }

    /**
     * 领取优惠券
     * @return
     */
    @Security.Authenticated(UserAjaxAuth.class)
    public F.Promise<Result> couponRec(){
        JsonNode rJson = request().body().asJson();

        Long recCouponId=rJson.findValue("recCouponId").asLong();

        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(SHOPPING_COUPON_REC+recCouponId).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            }
            else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("领取优惠券数据-->\n"+json);
            }
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误json=" + json);
                return badRequest();
            }
            return ok(toJson(message));
        });

    }


}
