package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.Message;
import domain.ThemeBasic;
import domain.ThemeItem;
import filters.UserAjaxAuth;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static modules.SysParCom.GOODS_PAGE;
import static play.libs.Json.toJson;
import static util.GZipper.dealToString;

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

    //H5页面
    public F.Promise<Result> h5page(Long themeId,String openType) {
        comCtrl.h5OpbeforeRender(ctx());
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request = comCtrl.getBuilder(ctx())
                    .url(THEME_PAGE + themeId)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("h5page接收数据-->\n"+json);
            }
            String themeImg = "";
            List<Object[]> tagList = new ArrayList<>();
            List<List<ThemeItem>> itemResultList = new ArrayList<>();
            ThemeBasic themeBasic = new ThemeBasic();
            if (json.has("themeList")) {
                themeBasic = Json.fromJson(json.get("themeList"), ThemeBasic.class);
                if(null!=themeBasic.getThemeImg()){
                    List<String> imgList = new ObjectMapper().readValue(themeBasic.getThemeImg(), new ObjectMapper().getTypeFactory().constructCollectionType(List.class, String.class));
                    themeBasic.setImgList(imgList);

                }
                //主题标签
                if (themeBasic.getMasterItemTag() != null) {
                    JsonNode tagJson = Json.parse(themeBasic.getMasterItemTag());
                    for (JsonNode tag : tagJson) {
                        try {
                            Object[] tagObject = new Object[5];
                            tagObject[0] = tag.get("top").asDouble() * 100;
                            tagObject[1] = tag.get("left").asDouble() * 100;
                            tagObject[2] = tag.get("width").asDouble() * 100;
                            tagObject[3] = tag.get("height").asDouble() * 100;
                            String tagUrl = Json.fromJson(tag.get("url"), String.class);
                            tagUrl = tagUrl.replace(GOODS_PAGE, "");
                            tagObject[4] = tagUrl;
                            tagList.add(tagObject);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            return ok(views.html.shopping.h5.h5page.render(themeBasic, tagList,openType));
        });
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

     public Result hp10(String openType) {
            comCtrl.h5OpbeforeRender(ctx());
            return ok(views.html.shopping.h5.hp10.render(openType));
     }

     public Result hp11(String openType) {
                 comCtrl.h5OpbeforeRender(ctx());
                 return ok(views.html.shopping.h5.hp11.render(openType));
          }

     public Result hp12(String openType) {
                 comCtrl.h5OpbeforeRender(ctx());
                 return ok(views.html.shopping.h5.hp12.render(openType));
          }

     public Result hp13(String openType) {
                     comCtrl.h5OpbeforeRender(ctx());
                     return ok(views.html.shopping.h5.hp13.render(openType));
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
