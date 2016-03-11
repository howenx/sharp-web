package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.Theme;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

import static modules.SysParCom.*;

/**
 * 购物车,结算相关
 * Created by howen on 16/3/9.
 */
public class ShoppingCtrl extends Controller {

    //全部订单
    public Result all() {
        return ok(views.html.shopping.all.render());
    }

    //待评价订单
    public Result appraise() {
        return ok(views.html.shopping.appraise.render());
    }

    //发表评价
    public Result assess() {
        return ok(views.html.shopping.assess.render());
    }

    public Result cart() {
        return ok(views.html.shopping.cart.render());
    }

    //待发货
    public Result delivered() {
        return ok(views.html.shopping.delivered.render());
    }

    //退款
    public Result drawback() {
        return ok(views.html.shopping.drawback.render());
    }

    //我的拼团
    public Result fightgroups() {
        return ok(views.html.shopping.fightgroups.render());
    }

    public Result logistic() {
        return ok(views.html.shopping.logistics.render());
    }

    public Result obligati() {
        return ok(views.html.shopping.obligation.render());
    }

    public Result orders() {
        return ok(views.html.shopping.orders.render());
    }

    public Result pinList(String url) throws Exception {
        List<Object[]> tagList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(IHEME_PAGE + url)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            JsonNode json = Json.parse(response.body().string());
            if(json.has("themeList")){
                JsonNode themeJson = json.get("themeList");
                if(themeJson.has("masterItemTag")){
                    JsonNode tagJson = themeJson.get("masterItemTag");
                    Logger.error("");
                    for(JsonNode tag : tagJson){
                        Logger.error(tag.toString());
                        Object[] tagObject = new Object[5];
                        tagObject[0] = tag.get("top").asDouble();
                        tagObject[1] = tag.get("url").toString();
                        tagObject[2] = tag.get("left").asDouble();
                        tagObject[3] = tag.get("name").toString();
                        tagObject[4] = tag.get("angle").asInt();
                        tagList.add(tagObject);
                    }
                    Logger.error(tagList.toString());
                }
            }

        }
        return ok(views.html.shopping.pinList.render());
    }

    public Result settle() {
        return ok(views.html.shopping.settle.render());
    }
}
