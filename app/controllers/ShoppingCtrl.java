package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.*;
import domain.CartListResultVo;
import domain.Message;
import filters.UserAuth;
import play.Logger;
import play.data.Form;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static play.libs.Json.toJson;

/**
 * 购物车,结算相关
 * Created by howen on 16/3/9.
 */
public class ShoppingCtrl extends Controller {
    @Inject
    ComCtrl comCtrl;

    //全部订单
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  all(Long id) {
        play.libs.F.Promise<JsonNode > promiseOfInt = play.libs.F.Promise.promise(() -> {
            Request.Builder builder =(Request.Builder)ctx().args.get("request");
            Request request=builder.url(ORDER_PAGE+id).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                return Json.parse(new String(response.body().bytes(), UTF_8));
            }else  throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((play.libs.F.Function<JsonNode , Result>) json -> {
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if(null==message||message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                Logger.error("返回收藏数据错误code="+(null!=message?message.getCode():0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<OrderDTO> orderList = mapper.readValue(json.get("orderList").toString(), new TypeReference<List<OrderDTO>>() {});
            if(null!=orderList&&!orderList.isEmpty()){
                for(OrderDTO orderDTO:orderList){
                    for(CartSkuDto sku:orderDTO.getSku()){
                        sku.setInvImg(comCtrl.getImgUrl(sku.getInvImg()));
                        sku.setInvUrl(comCtrl.getDetailUrl(sku.getInvUrl()));
                    }
                }
            }

            if (id > 0) {
                return ok(views.html.shopping.orderpa.render(orderList));//订单详情
            }
            return ok(views.html.shopping.all.render(orderList));
        });
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


    public Result logistic() {
        return ok(views.html.shopping.logistics.render());
    }

    public Result obligati() {
        return ok(views.html.shopping.obligation.render());
    }

    public Result orders() {
        return ok(views.html.shopping.orders.render());
    }

    /**
     * 商品结算
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result settle() {
        Logger.info("=对对对=="+Form.form().bindFromRequest());
        Logger.info("==="+Form.form().bindFromRequest().data());
        Map<String, String> userMap = Form.form().bindFromRequest().data();



        return ok(views.html.shopping.settle.render()
        );
    }




    /**
     * 取消订单
     * @param id
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  cancelOrder(Long id) {
        play.libs.F.Promise<JsonNode > promiseOfInt = play.libs.F.Promise.promise(() -> {
            Request.Builder builder =(Request.Builder)ctx().args.get("request");
            Request request=builder.url(ORDER_CANCEL+id).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                return Json.parse(new String(response.body().bytes(), UTF_8));
            }else  throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((play.libs.F.Function<JsonNode , Result>) json -> {
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if(null==message){
                        return badRequest();
                    }
                    return ok(toJson(message));
        });
    }

    /**
     * 删除订单
     * @param id
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  delOrder(Long id) {
        play.libs.F.Promise<JsonNode > promiseOfInt = play.libs.F.Promise.promise(() -> {
            Request.Builder builder =(Request.Builder)ctx().args.get("request");
            Request request=builder.url(ORDER_DEL+id).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                return Json.parse(new String(response.body().bytes(), UTF_8));
            }else  throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((play.libs.F.Function<JsonNode , Result>) json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if(null==message){
                return badRequest();
            }
            return ok(toJson(message));
        });
    }


}

