package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.CartListResultVo;
import domain.Message;
import filters.UserAuth;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
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

    /**
     * 购物车列表
     *
     * @return page
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> cart() {

        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(SHOPPING_LIST).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                Logger.info("===json==\n" + json);
                return json;
            } else throw new IOException("Unexpected code " + response);
        });

        return promise.map((F.Function<JsonNode, Result>) json -> {

                    String path = routes.ProductsCtrl.index().url();
                    if (session().containsKey("path")) {
                        path = session().get("path");
                        session().replace("path", routes.ShoppingCtrl.cart().url());
                    }else session().put("path", routes.ShoppingCtrl.cart().url());

                    CartListResultVo resultVo = Json.fromJson(json, CartListResultVo.class);
                    if (resultVo.getMessage().getCode() == Message.ErrorCode.SUCCESS.getIndex()) {
                        return ok(views.html.shopping.cart.render(path, resultVo));
                    } else if (resultVo.getMessage().getCode() == Message.ErrorCode.CART_LIST_NULL_EXCEPTION.getIndex()) {
                        return ok(views.html.shopping.cartempty.render(path));
                    } else return badRequest(views.html.error500.render());
                }
        );
    }

    /**
     * 空购物车页面
     *
     * @return page
     */
    public Result emptyCart() {
        String path = routes.ProductsCtrl.index().url();
        if (session().containsKey("path")) {
            path = session().get("path");
            session().replace("path", routes.ShoppingCtrl.emptyCart().url());
        }else session().put("path", routes.ShoppingCtrl.cart().url());
        return ok(views.html.shopping.cartempty.render(path));
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
        String themeImg = "";
        List<Object[]> tagList = new ArrayList<>();
        List<Object[]> itemList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(THEME_PAGE + url)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            JsonNode json = Json.parse(response.body().string());
            if(json.has("themeList")){
                JsonNode themeJson = json.get("themeList");
                //主题主图
                if(themeJson.has("themeImg")){
                    String img = Json.fromJson(themeJson.get("themeImg"),String.class);
                    JsonNode imgJson = Json.parse(img);
                    themeImg = imgJson.get("url").toString();
                    themeImg = themeImg.substring(1,themeImg.length()-1);
                }
                //主题标签
                if(themeJson.has("masterItemTag")){
                    JsonNode tempJson = themeJson.get("masterItemTag");

                    String tags = Json.fromJson(tempJson,String.class);
                    if(tags != null){
                        JsonNode tagJson = Json.parse(tags);
                        for(JsonNode tag : tagJson){
                            Object[] tagObject = new Object[6];
                            tagObject[0] = tag.get("top").asDouble() * 100;
                            tagObject[1] = tag.get("url").toString();
                            tagObject[2] = tag.get("left").asDouble() * 100;
                            String tagName = tag.get("name").toString();
                            tagName = tagName.substring(1,tagName.length()-1);
                            tagObject[3] = tagName;
                            tagObject[4] = tag.get("angle").asInt();
                            if(tagObject[4].equals(0)){
                                tagObject[5] = tag.get("left").asDouble() * 100 + 5 ;
                            }
//                            if(tagObject[4].equals(180)){
//                                tagObject[5] = tag.get("left").asDouble() * 100;
//                            }

                            tagList.add(tagObject);
                        }
                    }
                }
                //主题中的商品
                if(themeJson.has("themeItemList")){
                    JsonNode itemJson = themeJson.get("themeItemList");
                    for(JsonNode tempJson : itemJson){
                        Object[] itemObject = new Object[7];
                        String itemImg = Json.fromJson(tempJson.get("itemImg"),String.class);
                        JsonNode itemImgJson = Json.parse(itemImg);
                        String itemImgUrl = itemImgJson.get("url").toString();
                        itemImgUrl = itemImgUrl.substring(1,itemImgUrl.length()-1);
                        itemObject[0] = itemImgUrl;                                         //商品图片
                        itemObject[1] = tempJson.get("itemType");                           //商品类型
                        String itemType = tempJson.get("itemType").toString();
                        itemType = itemType.substring(1,itemType.length()-1);
                        String itemUrl = tempJson.get("itemUrl").toString();
                        itemUrl = itemUrl.substring(1,itemUrl.length()-1);
                        if("pin".equals(itemType)){
                            //itemUrl = itemUrl.replace("http://172.28.3.51:9001/comm/pin/detail/","");
                            itemUrl = itemUrl.replace(PIN_PAGE,"");
                        }
                        if("item".equals(itemType)){
                            //itemUrl = itemUrl.replace("http://172.28.3.51:9001/comm/detail/","");
                            itemUrl = itemUrl.replace(ITEM_PAGE,"");
                        }
                        itemObject[2] = itemUrl;                                            //商品链接
                        itemObject[3] = tempJson.get("itemTitle");                          //商品Title
                        itemObject[4] = tempJson.get("itemSrcPrice");                       //商品原价
                        itemObject[5] = tempJson.get("itemPrice");                          //商品价格
                        itemObject[6] = tempJson.get("itemDiscount");                       //商品折扣
                        itemList.add(itemObject);
                    }
                }
            }
        }
        return ok(views.html.shopping.pinList.render(themeImg,tagList,itemList));
    }

    public Result settle() {
        return ok(views.html.shopping.settle.render());
    }
}
