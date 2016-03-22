package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.Message;
import filters.UserAuth;
import modules.SysParCom;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.client;
import static play.libs.Json.toJson;

/**
 * Created by sibyl.sun on 16/3/19.
 */
public class JDCtrl extends Controller {
    @Inject
    ComCtrl comCtrl;

    @Security.Authenticated(UserAuth.class)
    public Result cashDesk(Long orderId){
//        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
//            Request.Builder builder = (Request.Builder) ctx().args.get("request");
//            Request request = builder.url(SysParCom.PAY_ORDER+orderId).get().build();
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                return Json.parse(new String(response.body().bytes(), UTF_8));
//            } else throw new IOException("Unexpected code " + response);
//        });
//        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
//
//            return ok(toJson(json));
//        } );
        response().setHeader("id-token",session().get("id-token"));
        Logger.info("==session().get(\"id-token\")=="+session().get("id-token"));
        return redirect(SysParCom.PAY_ORDER+orderId);
    }

}
