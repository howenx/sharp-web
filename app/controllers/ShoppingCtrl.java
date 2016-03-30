package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.*;
import filters.UserAuth;
import play.Logger;
import play.data.Form;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.Crypto;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static play.libs.Json.toJson;
import static util.GZipper.dealToString;
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

        String token=session().get("id-token");
        return promiseOfInt.map((play.libs.F.Function<JsonNode , Result>) json -> {
        //    Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误code=" + json);
                return badRequest(views.html.error500.render());
            }
            if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                Logger.info("返回数据code=" + json);
                return badRequest(views.html.error.render(message.getMessage()));
            }
            ObjectMapper mapper = new ObjectMapper();
            List<OrderDTO> orderList = mapper.readValue(json.get("orderList").toString(), new TypeReference<List<OrderDTO>>() {});
            if(null!=orderList&&!orderList.isEmpty()){
                for(OrderDTO orderDTO:orderList){
                    for(CartSkuDto sku:orderDTO.getSku()){
                        sku.setInvImg(comCtrl.getImgUrl(sku.getInvImg()));
                        sku.setInvUrl(comCtrl.getDetailUrl(sku.getInvUrl()));
                    }
                    orderDTO.getOrder().setSecurityCode(comCtrl.orderSecurityCode(orderDTO.getOrder().getOrderId()+"",token));
                }
            }

            if (id > 0) {
                return ok(views.html.shopping.orderpa.render(orderList,PAY_URL,token));//订单详情
            }
            return ok(views.html.shopping.all.render(orderList,PAY_URL,token));
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

            //数据量过大情况下,可以加上这一句
//            builder.addHeader("Accept-Encoding","gzip");

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if(result!=null){
                    JsonNode json = Json.parse(result);
             //       Logger.info("===json==\n" + json);
                    return json;
                } else throw new IOException("Unexpected code " + response);
            } else throw new IOException("Unexpected code " + response);
        });

        return promise.map(json -> {

                    String path = routes.ProductsCtrl.index().url();
                    if (session().containsKey("path")) {
                        path = session().get("path");
                        session().replace("path", routes.ShoppingCtrl.cart().url());
                    }else session().put("path", routes.ShoppingCtrl.cart().url());

                    CartListResultVo resultVo = Json.fromJson(json, CartListResultVo.class);
                    if (resultVo.getMessage().getCode() == Message.ErrorCode.SUCCESS.getIndex()) {
                        for(CartItemDTO cart:resultVo.getCartList()){
                            for(CartListDto sku:cart.getCarts()){
                                sku.setInvImg(comCtrl.getImgUrl(sku.getInvImg()));
                                sku.setInvUrl(comCtrl.getDetailUrl(sku.getInvUrl()));
                            }
                        }
                        return ok(views.html.shopping.cart.render(path, resultVo));
                    } else if (resultVo.getMessage().getCode() == Message.ErrorCode.CART_LIST_NULL_EXCEPTION.getIndex()) {
                        return ok(views.html.shopping.cartempty.render(path));
                    } else return badRequest(views.html.error500.render());
                }
        );
    }

//    /**
//     * 添加商品到购物车
//     * @return
//     */
//    @Security.Authenticated(UserAuth.class)
//    public F.Promise<Result> addToCart(){
//        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
//            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, request().body().asJson().toString());
//            Request.Builder builder = (Request.Builder) ctx().args.get("request");
//            Request request = builder.url(SHOPPING_ADDTOCART).post(formBody).build();
//            Response response = client.newCall(request).execute();
//            if (response.isSuccessful()) {
//                String result = dealToString(response);
//                if (result != null) {
//                    return Json.parse(result);
//                } else throw new IOException("Unexpected code" + response);
//            } else throw new IOException("Unexpected code" + response);
//        });
//        return promise.map((F.Function<JsonNode, Result>) json -> {
//                    Logger.error(json.toString());
//                    return ok("AAAA");
//                }
//        );
//    }

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
    public F.Promise<Result> settle() {
        ObjectNode result = Json.newObject();
     //   Logger.info("==settle data="+Form.form().bindFromRequest().data());
        Map<String, String> settleMap = Form.form().bindFromRequest().data();
        Logger.error(settleMap.toString());
        Integer buyNow=Integer.valueOf(settleMap.get("buyNow"));//1－立即支付 2-购物车结算
        Map<String,Object> object=new HashMap<>();

        List<SettleDTO> settleDTOs=new ArrayList<SettleDTO>();

        List<SettleInfo> settleInfoList=new ArrayList<SettleInfo>();
        boolean isPin=false;//是否是拼团
        boolean isPinCheck=false;//是否是拼团校验
        if(null!=settleMap.get("isPinCheck")){
            isPin=true;
            if(1==Integer.valueOf(settleMap.get("isPinCheck"))){
                isPinCheck=true;
            }
        }
        Integer areaNum=1;
        if(buyNow==2){
            areaNum=Integer.valueOf(settleMap.get("areaNum"));
        }
        for(int i=0;i<areaNum;i++){
            if(null==settleMap.get("invCustoms"+i)){
            //    Logger.info("第"+(i+1)+"个保税区未选");
                continue;
            }
            String invCustoms=settleMap.get("invCustoms"+i);  //保税区
            String invArea=settleMap.get("invArea"+i);//保税区
            String invAreaNm=settleMap.get("invAreaNm"+i);//保税区
            Integer cartNum=1;
            if(null!=settleMap.get("cartNum"+i)){
                cartNum=Integer.valueOf(settleMap.get("cartNum"+i));
            }
            List<CartDto>cartDtos=new ArrayList<CartDto>();
            List<CartInfo>cartInfos=new ArrayList<CartInfo>();
            for(int j=0;j<cartNum;j++){
                String suffix=i+"-"+j;
                if(null==settleMap.get("skuId"+suffix)){
            //        Logger.info("第"+(i+1)+"个保税区下的第"+(j+1)+"个商品未选");
                    continue;
                }
                Long cartId=0L;
                if(null!=settleMap.get("cartId"+suffix)){
                    cartId=Long.valueOf(settleMap.get("cartId"+suffix)); //购物车ID
                }
                Long skuId=Long.valueOf(settleMap.get("skuId"+suffix));
                Integer amount=1;
                if(null!=settleMap.get("amount"+suffix)){
                    amount=Integer.valueOf(settleMap.get("amount"+suffix));//购买的数量
                }
                String state=settleMap.get("state"+suffix); //商品的状态

                String skuType=settleMap.get("skuType"+suffix);

                if(buyNow==1&&!isPin&&"pin".equals(skuType)){ //拼购下的单独购买skuType为item
                    skuType="item";
                }
                Long skuTypeId=Long.valueOf(settleMap.get("skuTypeId"+suffix));//商品类型的id

                Long pinTieredPriceId=0L;
                if(null!=settleMap.get("pinTieredPriceId"+suffix)){
                    pinTieredPriceId=Long.valueOf(settleMap.get("pinTieredPriceId"+suffix));//在提交拼购商品订单时填写阶梯价格的id
                }
                String skuTitle=settleMap.get("skuTitle"+suffix);   //商品标题,用于展示
                String skuInvImg=settleMap.get("skuInvImg"+suffix); //商品图片,用于展示
                String skuPrice="";
                if(null!=settleMap.get("skuPrice"+suffix)) {
                    skuPrice=settleMap.get("skuPrice"+suffix);   //商品价格,用于展示
                }
                CartDto cartDTO=new CartDto(cartId,skuId,amount,state,skuType,skuTypeId,pinTieredPriceId);
                cartDtos.add(cartDTO);
                CartInfo cartInfo=new CartInfo(cartId,skuId,amount,state,skuType,skuTypeId,pinTieredPriceId,skuTitle,skuInvImg,skuPrice);
                cartInfos.add(cartInfo);
            }
            if(cartDtos.size()<=0){
                Logger.error("商品结算第"+(i+1)+"个保税区无商品"+Json.toJson(settleMap));
                continue;
            }

            SettleDTO settleDTO=createSettleDTO(invCustoms,invArea,invAreaNm,cartDtos);
            settleDTOs.add(settleDTO);
            SettleInfo settleInfo=new SettleInfo();
            settleInfo.setInvCustoms(invCustoms);
            settleInfo.setInvArea(invArea);
            settleInfo.setInvAreaNm(invAreaNm);
            settleInfo.setCartInfos(cartInfos);
            settleInfoList.add(settleInfo);
        }
        if(settleDTOs.size()<=0){
            Logger.error("商品结算无商品"+Json.toJson(settleMap));
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return F.Promise.promise((F.Function0<Result>) () -> ok(result));
        }

        object.put("settleDTOs",settleDTOs);

        Long addressId=0L;
        if(null!=settleMap.get("addressId")){
            addressId=Long.valueOf(settleMap.get("addressId"));
        }
        object.put("addressId",addressId);//地址id
        Long couponId=0L;
        if(null!=settleMap.get("couponId")){
            couponId=Long.valueOf(settleMap.get("couponId"));
        }
        object.put("couponId",couponId);//优惠券id
        object.put("clientIp",request().remoteAddress());//客户端ip
        object.put("clientType","3");
        object.put("shipTime",1); //送货日期：1－工作日双休日与假期均可送货 2-只工作日送货 3-只双休日送货
        String payMethod="JD";
        if(null!=settleMap.get("payMethod")){
            payMethod=settleMap.get("payMethod");
        }
        object.put("payMethod",payMethod);
        object.put("buyNow",buyNow);//1－立即支付 2-购物车结算
        Long pinActiveId=0L;
        if(null!=settleMap.get("pinActiveId")){
            pinActiveId=Long.valueOf(settleMap.get("pinActiveId"));
        }

        object.put("pinActiveId",pinActiveId); //拼购活动id


        RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, toJson(object).toString());
        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(SHOPPING_SETTLE).post(formBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code" + response);
        });
        final Long finalPinActiveId = pinActiveId;
        final Integer buyNowTemp=buyNow;
        final boolean finalIsPinCheck = isPinCheck;
        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
            Logger.info("==settle=json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回商品结算数据错误code=" + json);
                return badRequest(views.html.error500.render());
            }
            if(finalIsPinCheck){
                return ok(Json.toJson(message));
            }
            if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                Logger.info("返回数据code=" + json);
                return badRequest(views.html.error.render(message.getMessage()));
            }
            SettleVo settleVo=Json.fromJson(json.get("settle"), SettleVo.class);
            Logger.info(String.valueOf(toJson(settleVo)));

            return ok(views.html.shopping.settle.render(settleVo,settleInfoList,buyNowTemp, finalPinActiveId,PAY_URL));
        });
    }
    private SettleDTO createSettleDTO(String invCustoms,String invArea, String invAreaNm,List<CartDto> cartDtos){
        SettleDTO settleDTO=new SettleDTO();
        settleDTO.setInvCustoms(invCustoms);
        settleDTO.setInvArea(invArea);
        settleDTO.setInvAreaNm(invAreaNm);
        settleDTO.setCartDtos(cartDtos);
        return settleDTO;
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
    /**
     * 订单校验
     * @param orderId
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  verifyOrder(Long orderId) {
        return comCtrl.getReqReturnMsg(ORDER_VERIFY+orderId);
    }

    /**
     * 订单提交
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  submitOrder() {
        ObjectNode result = Json.newObject();
        Logger.info("==submitOrder=\n"+Form.form().bindFromRequest().data());
        Map<String, String> settleMap = Form.form().bindFromRequest().data();
        Map<String,Object> object=new HashMap<>();
        List<SettleDTO> settleDTOs=new ArrayList<SettleDTO>();
        Integer areaNum=Integer.valueOf(settleMap.get("areaNum"));
        for(int i=0;i<areaNum;i++){
            String invCustoms=settleMap.get("invCustoms"+i);  //保税区
            String invArea=settleMap.get("invArea"+i);//保税区
            String invAreaNm=settleMap.get("invAreaNm"+i);//保税区
            Integer cartNum=Integer.valueOf(settleMap.get("cartNum"+i));
            List<CartDto>cartDtos=new ArrayList<CartDto>();
            for(int j=0;j<cartNum;j++){
                String suffix=i+"-"+j;
                Long cartId=Long.valueOf(settleMap.get("cartId"+suffix));
                Long skuId=Long.valueOf(settleMap.get("skuId"+suffix));
                Integer amount=Integer.valueOf(settleMap.get("amount"+suffix));//购买的数量
                String state=settleMap.get("state"+suffix); //商品的状态
                String skuType=settleMap.get("skuType"+suffix);
                Long skuTypeId=Long.valueOf(settleMap.get("skuTypeId"+suffix));//商品类型的id
                Long pinTieredPriceId=Long.valueOf(settleMap.get("pinTieredPriceId"+suffix));//在提交拼购商品订单时填写阶梯价格的id
                CartDto cartDTO=new CartDto(cartId,skuId,amount,state,skuType,skuTypeId,pinTieredPriceId);
                cartDtos.add(cartDTO);
            }
            if(cartDtos.size()<=0){
                Logger.error("订单提交第"+(i+1)+"个保税区无商品"+Json.toJson(settleMap));
                continue;
            }
            SettleDTO settleDTO=createSettleDTO(invCustoms,invArea,invAreaNm,cartDtos);
            settleDTOs.add(settleDTO);
        }
        object.put("settleDTOs",settleDTOs);
        if(settleDTOs.size()<=0){
            Logger.error("订单提交无商品"+Json.toJson(settleMap));
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return F.Promise.promise((F.Function0<Result>) () -> ok(result));
        }

        object.put("addressId",Long.valueOf(settleMap.get("addressId")));//地址id
        object.put("couponId",Long.valueOf(settleMap.get("couponId")));//优惠券id
        object.put("clientIp",request().remoteAddress());//客户端ip
        object.put("clientType","3");
        object.put("shipTime",Integer.valueOf(settleMap.get("shipTime"))); //送货日期：1－工作日双休日与假期均可送货 2-只工作日送货 3-只双休日送货
        object.put("payMethod",settleMap.get("payMethod"));
        object.put("buyNow",Integer.valueOf(settleMap.get("buyNow")));//1－立即支付 2-购物车结算
        Long pinActiveId=Long.valueOf(settleMap.get("pinActiveId"));
        object.put("pinActiveId",pinActiveId); //拼购活动id

        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, toJson(object).toString());
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(ORDER_SUBMIT).post(formBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code" + response);
        });

        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
           Logger.info("==submitOrder=json=" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回商品结算数据错误code=" + json);
                return badRequest();
            }

            ObjectNode objectNode = Json.newObject();
            objectNode.putPOJO("message",message);
            if(message.getCode()==Message.ErrorCode.SUCCESS.getIndex()&&json.has("orderId")){
                String securityCode= comCtrl.orderSecurityCode(json.get("orderId").asText(),session().get("id-token"));
                objectNode.put("token",session().get("id-token"));
                objectNode.put("orderId",json.get("orderId").asLong());
                objectNode.put("securityCode",securityCode);
            }
            return ok(toJson(objectNode));
        });
    }

    /**
     * 用户将本地购物车添加到网络购物车中（POST请求）
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  cartAdd(){
        JsonNode json = request().body().asJson();
//        Logger.info("==json==="+json);
        List<CartAddInfo> cartAddInfoList=new ArrayList<CartAddInfo>();
        CartAddInfo cartAddInfo=Json.fromJson(json,CartAddInfo.class);
        cartAddInfoList.add(cartAddInfo);
        RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, toJson(cartAddInfoList).toString());
        return comCtrl.postReqReturnMsg(CART_ADD,formBody);
    }

    /**
     * 删除购物车
     * @param cartId
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result>  cartDel(Long cartId){
        return comCtrl.getReqReturnMsg(CART_DEL+cartId);
    }

    /**
     * 购物车数量
     * @return
     */
    public F.Promise<Result>  cartAmount(){
        Optional<String> header = Optional.ofNullable(ctx().session().get("id-token"));
        if (header.isPresent()) {
                F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
                    Request.Builder builder = new Request.Builder();
                    builder.addHeader(Http.HeaderNames.X_FORWARDED_FOR,ctx().request().remoteAddress());
                    builder.addHeader(Http.HeaderNames.VIA,ctx().request().remoteAddress());
                    builder.addHeader("User-Agent",ctx().request().getHeader("User-Agent"));
                    builder.addHeader("id-token", header.get());

                    Request request = builder.url(CART_AMOUNT).get().build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return Json.parse(new String(response.body().bytes(), UTF_8));
                    } else throw new IOException("Unexpected code" + response);
                });

                return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
                 //   Logger.info("==settle=json==" + json);
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if (null == message) {
                        Logger.error("返回商品结算数据错误code=" + json);
                        return badRequest();
                    }
                    return ok(json);
                });

        }
        ObjectNode result = Json.newObject();
        result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.FAILURE.getIndex()), Message.ErrorCode.FAILURE.getIndex())));
        return F.Promise.promise((F.Function0<Result>) () -> ok(result));
    }

}

