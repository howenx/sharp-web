package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.*;
import filters.UserAuth;
import modules.ComTools;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.api.libs.Codecs;
import play.cache.Cache;
import play.data.Form;
import play.libs.F;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import sun.misc.BASE64Encoder;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static play.libs.Json.newObject;
import static play.libs.Json.toJson;


/**
 * 用户相关
 * Created by howen on 16/3/9.
 */
public class UserCtrl extends Controller {

    //将Json串转换成List
    public final static ObjectMapper mapper = new ObjectMapper();

    @Inject
    private MemcachedClient cache;
    @Inject
    ComCtrl comCtrl;



    //收货地址
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> address(Long selId) {

        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(ADDRESS_PAGE).get().build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            String path = routes.UserCtrl.myView().url();
            if (session().containsKey("path")) {
                //path = session().get("path");
                session().replace("path", routes.UserCtrl.address(selId).url());
            }else session().put("path", routes.UserCtrl.address(selId).url());

            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || (Message.ErrorCode.SUCCESS.getIndex() != message.getCode() && Message.ErrorCode.DATABASE_EXCEPTION.getIndex() != message.getCode())) {
                Logger.error("返回地址数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            if(selId==1){
                return ok(json);
            }
            //空地址列表
            if (Message.ErrorCode.DATABASE_EXCEPTION.getIndex() == message.getCode()) {
                return ok(views.html.users.addressempty.render(path, selId));
            } else if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                //地址列表
                ObjectMapper mapper = new ObjectMapper();
                List<Address> addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {
                });
                return ok(views.html.users.address.render(path,addressList,selId));
            } else return badRequest(views.html.error500.render());
        });
    }


    /**
     * 创建新的收货地址
     *
     * @return
     */
    public Result addressnew(Long selId) {
        String path = routes.UserCtrl.address(selId).url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.addressnew(selId).url());
        }else session().put("path", routes.UserCtrl.addressnew(selId).url());
        return ok(views.html.users.addressnew.render(path, selId));
    }

    //创建新的收货地址
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> addressSave() {

        ObjectNode result = Json.newObject();
        Form<AddressInfo> addressForm = Form.form(AddressInfo.class).bindFromRequest();
//        Logger.info("====addressSave===\n" + addressForm.data());
        Map<String, String> addressMap = addressForm.data();
        String idCardNum = addressMap.get("idCardNum").trim().toLowerCase();
        if (addressForm.hasErrors() || !"".equals(ComTools.IDCardValidate(idCardNum))) { //表单错误或者身份证校验不通过
            Logger.error("收货地址保存表单错误或者身份证校验不通过"+toJson(addressMap));
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            ObjectNode object = Json.newObject();
            Long addId = Long.valueOf(addressMap.get("addId"));
            if(addId>0){
                object.put("addId", addId);
            }
            String province = addressMap.get("province");
            if (!"0".equals(province)) {//未修改省份
                ObjectNode cityObject = Json.newObject();
                cityObject.put("province", addressMap.get("province"));
                cityObject.put("city", addressMap.get("city"));
                cityObject.put("area", addressMap.get("area"));
                cityObject.put("province_code", addressMap.get("province_code"));
                cityObject.put("area_code", addressMap.get("area_code"));
                cityObject.put("city_code", addressMap.get("city_code"));
                object.put("deliveryCity", cityObject.toString());
            }
            object.put("tel", addressMap.get("tel").trim());
            object.put("name", addressMap.get("name").trim());
            object.put("deliveryDetail", addressMap.get("deliveryDetail").trim());
            object.put("orDefault", "on".equals(addressMap.get("orDefault")) ? 1 : 0);
            object.put("idCardNum", idCardNum);


            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, object.toString());
                Request.Builder builder = (Request.Builder) ctx().args.get("request");
                Request request = builder.url(addId > 0 ? ADDRESS_UPDATE : ADDRESS_ADD).post(formBody).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {
 //               Logger.info("===json==" + json);
                Message message = Json.fromJson(json.get("message"), Message.class);
                if (null == message) {
                    Logger.error("返回创建新的收货地址数据错误code=" + json);
                    return badRequest();
                }
                return ok(toJson(message));
            });
        }
    }

    /**
     * 更新地址界面
     * @param addId
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> addressUpdate(Long addId, Long selId) {

        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(ADDRESS_PAGE).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            String path = routes.UserCtrl.address(addId).url();
            if (session().containsKey("path")) {
                //path = session().get("path");
                session().replace("path", routes.UserCtrl.addressUpdate(addId, selId).url());
            }else session().put("path", routes.UserCtrl.addressUpdate(addId, selId).url());

//            Logger.error("返回---->\n" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误code=" + json);
                return badRequest(views.html.error500.render());
            }
            if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                Logger.error("返回数据code=" + json);
                return badRequest(views.html.error.render(message.getMessage()));
            }
            ObjectMapper mapper = new ObjectMapper();
            List<Address> addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {
            });
            for (Address address : addressList) {
                if (address.getAddId() == addId.longValue()) {
                    return ok(views.html.users.addressupdate.render(path,address,selId));
                }
            }
            return badRequest();
        });
    }

    /***
     * 地址删除
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> addressDel() {
            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, request().body().asJson().toString());
            return comCtrl.postReqReturnMsg(ADDRESS_DEL, formBody);
    }

        //身份认证

    public Result carded() {
        return ok(views.html.users.carded.render());
    }

    //优惠券
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> coupon() {
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(COUPON_PAGE).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));

            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
   //         Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误code=" + json);
                return badRequest(views.html.error500.render());
            }
            if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                Logger.error("返回数据code=" + json);
                return badRequest(views.html.error.render(message.getMessage()));
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CouponVo> couponList = mapper.readValue(json.get("coupons").toString(), new TypeReference<List<CouponVo>>() {
            });
            return ok(views.html.users.coupon.render(couponList));
        });
    }

    /**
     * 用户登录
     *
     * @return
     */
    public Result login() {
        String path = routes.ProductsCtrl.index().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.login().url());
        }else session().put("path", routes.UserCtrl.login().url());
        return ok(views.html.users.login.render(path, IMAGE_CODE));
    }

    /**
     * 我的界面
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> myView() {
            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                Request.Builder builder =(Request.Builder)ctx().args.get("request");
                Request request=builder.url(USER_INFO).get().build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()){
                    return Json.parse(new String(response.body().bytes(), UTF_8));

                }else  throw new IOException("Unexpected code " + response);
            });

            return promiseOfInt.map((Function<JsonNode , Result>) json -> {
                String path = routes.ProductsCtrl.index().url();
                if (session().containsKey("path")) {
                    //path = session().get("path");
                    session().replace("path", routes.UserCtrl.myView().url());
                }else session().put("path", routes.UserCtrl.myView().url());
     //           Logger.info("==myView=json==" + json);
                Message message = Json.fromJson(json.get("message"), Message.class);
                if (null == message) {
                    Logger.error("返回数据错误code=" + json);
                    return badRequest(views.html.error500.render());
                }
                if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                    Logger.error("返回数据code=" + json);
                    return badRequest(views.html.error.render(message.getMessage()));
                }
                UserDTO userInfo = Json.fromJson(json.get("userInfo"), UserDTO.class);
                //请求用户信息
                return ok(views.html.users.my.render(path, userInfo)); //登录了
            });
    }

    /**
     * 意见反馈
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result tickling() {
        return ok(views.html.users.tickling.render());
    }

    /**
     * 意见反馈
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> feedback() {
        JsonNode rJson = request().body().asJson();
        String content = "";
        if (rJson.has("content")) {
            content = rJson.findValue("content").asText().trim();
        }
        if ("".equals(content) || content.length() > 140) {
            ObjectNode result = Json.newObject();
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        }
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, rJson.toString());
        return comCtrl.postReqReturnMsg(FEEDBACK_PAGE, requestBody);

    }

    @Security.Authenticated(UserAuth.class)
    public Result setting() {
        String path = routes.UserCtrl.myView().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.setting().url());
        }else session().put("path", routes.UserCtrl.setting().url());

//        Request.Builder builder = (Request.Builder) ctx().args.get("request");
//        Logger.error("session token----> " + session().get("id-token"));
//        Logger.error("Cache user----> " + cache.get(session().get("id-token")));
//        Logger.error(request().cookie("user_token").value());

        return ok(views.html.users.setting.render(path));
    }

    /**
     * 我的收藏
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> collect() {
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(COLLECT_PAGE).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
        //    Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CollectDto> collectList = mapper.readValue(json.get("collectList").toString(), new TypeReference<List<CollectDto>>() {});
            if(null!=collectList&&!collectList.isEmpty()){
                for(CollectDto collectDto:collectList){
                    collectDto.getCartSkuDto().setInvImg(comCtrl.getImgUrl(collectDto.getCartSkuDto().getInvImg()));
                    collectDto.getCartSkuDto().setInvUrl(comCtrl.getDetailUrl(collectDto.getCartSkuDto().getInvUrl()));

                }
            }
            return ok(views.html.users.collect.render(collectList));
        });
    }

    /**
     * 取消收藏
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> collectDel(Long collectId) {
        return comCtrl.getReqReturnMsg(COLLECT_DEL + collectId);
    }

    /**
     * 收藏
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> submitCollect(){
        ObjectNode result = newObject();
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, request().body().asJson().toString());
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(COLLECT_SUBMIT).post(formBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
        //    Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误code=" + json);
                return badRequest(views.html.error500.render());
            }
            if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                Logger.error("返回数据code=" + json);
                return badRequest(views.html.error.render(message.getMessage()));
            }
            Integer collectId=json.get("collectId").asInt();
            result.putPOJO("collectId",collectId);
            return ok(Json.toJson(result));
        });
    }


    /**
     * 用户登录提交
     *
     * @return
     */
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
                        if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                            String token = json.findValue("result").findValue("token").asText();
                            Integer expired = json.findValue("result").findValue("expired").asInt();
                            if (userMap.get("auto").equals("true")) {
                                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                                Cache.set(session_id, token, expired);
                                session("session_id", session_id);
                                response().setCookie("session_id", session_id, expired);
                                response().setCookie("user_token", token, expired);
                            }
                            session("id-token", token);
                        }
//                        Logger.error(json.toString()+"-----"+message.toString());
                        return ok(Json.toJson(message));
                    }
            );
        }
    }


    /**
     * 快速注册
     *
     * @return views
     */
    public Result registVerify() {
        String path = routes.UserCtrl.login().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.registVerify().url());
        }else session().put("path", routes.UserCtrl.registVerify().url());
        return ok(views.html.users.registVerify.render(path));
    }

    /**
     * 手机号检测
     *
     * @return
     */
    public F.Promise<Result> phoneVerify() {
        ObjectNode result = newObject();
        Form<UserPhoneVerify> userPhoneVerifyForm = Form.form(UserPhoneVerify.class).bindFromRequest();
        Map<String, String> userMap = userPhoneVerifyForm.data();
        if (userPhoneVerifyForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .url(PHONE_VERIFY)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                    return json;
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
                //Logger.error(json.toString()+"-----"+message.toString());
                return ok(Json.toJson(message));
            });
        }
    }


    /**
     * 请求手机验证码
     *
     * @return
     */
    public Promise<Result> phoneCode() {
        ObjectNode result = newObject();
        Form<UserPhoneCode> userPhoneCodeForm = Form.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userPhoneCodeForm.data();
        String phone = userMap.get("phone");
        userMap.put("msg", Codecs.md5((phone + "hmm").getBytes()));
        if (userPhoneCodeForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .url(PHONE_CODE)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                    return json;
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
                if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                    //Logger.error("验证码发送成功");
                }
                //Logger.error(json.toString()+"-----"+message.toString());
                return ok(Json.toJson(message));
            });
        }
    }

    /**
     * 注册
     *
     * @return views
     */
    public Result register() {
        String path = routes.UserCtrl.registVerify().url();
        if (session().containsKey("path")) {
//            path = session().get("path");
            session().replace("path", routes.UserCtrl.register().url());
        }else session().put("path", routes.UserCtrl.register().url());
        Form<UserPhoneCode> userPhoneCodeForm = Form.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userPhoneCodeForm.data();
        String phone = userMap.get("phone");
        return ok(views.html.users.regist.render(path, phone));
    }

    /**
     * 服务条款
     * @return views
     */
    public Result agreement() throws IOException {

        Request request = new Request.Builder()
                .url(VIEWS_AGREEMENT)
                .build();
        Response response = client.newCall(request).execute();
        String str = new String(response.body().bytes(), UTF_8);
        Logger.error("str:"+str);
        if (response.isSuccessful()) {
            return ok(views.html.users.agreement.render(str));
        } else throw new IOException("Unexpected code " + response);
    }

    /**
     * 用户注册提交
     *
     * @return
     */
    public Promise<Result> registSubmit() {
        ObjectNode result = newObject();
        Form<UserRegistInfo> userRegistInfoForm = Form.form(UserRegistInfo.class).bindFromRequest();
        Map<String, String> userMap = userRegistInfoForm.data();
        if (userRegistInfoForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .header("User-Agent", request().getHeader("User-Agent"))
                        .url(REGISTER_PAGE)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
                if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                    //注册成功请求登录接口
                    FormEncodingBuilder feb = new FormEncodingBuilder();
                    userMap.forEach(feb::add);
                    RequestBody formBody = feb.build();
                    Request request2 = new Request.Builder()
                            .header("User-Agent", request().getHeader("User-Agent"))
                            .url(LOGIN_PAGE)
                            .post(formBody)
                            .build();
                    client.setConnectTimeout(10, TimeUnit.SECONDS);
                    Response response2 = client.newCall(request2).execute();
                    if (response2.isSuccessful()) {
                        JsonNode json2 = Json.parse(new String(response2.body().bytes(), UTF_8));
                        Message message2 = Json.fromJson(json2.findValue("message"), Message.class);
                        if (Message.ErrorCode.SUCCESS.getIndex() == message2.getCode()) {
                            String token = json.findValue("result").findValue("token").asText();
                            Integer expired = json.findValue("result").findValue("expired").asInt();
                            String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                            Cache.set(session_id, token, expired);
                            session("session_id", session_id);
                            response().setCookie("session_id", session_id, expired);
                            response().setCookie("user_token", token, expired);
                            session("id-token", token);
                        }
                        return ok(Json.toJson(message2));
                    } else throw new IOException("Unexpected code" + response2);
                }
                //Logger.error(json.toString()+"-----"+message.toString());
                return ok(Json.toJson(message));
            });
        }

    }

    /**
     * 找回密码
     *
     * @return views
     */
    public Result retrieve() {
        String path = routes.UserCtrl.login().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.retrieve().url());
        }else session().put("path", routes.UserCtrl.retrieve().url());
        return ok(views.html.users.retrieve.render(path,IMAGE_CODE));
    }

    /**
     * 密码重置
     *
     * @return
     */
    public Result resetPasswd() {
        String path = routes.UserCtrl.retrieve().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.resetPasswd().url());
        }else session().put("path", routes.UserCtrl.resetPasswd().url());
        Form<UserPhoneCode> userPhoneCodeForm = Form.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userPhoneCodeForm.data();
        String phone = userMap.get("phone");
        return ok(views.html.users.resetPasswd.render(path, phone));
    }

    /**
     * 密码修改提交
     *
     * @return
     */
    public Promise<Result> resetPwdSubmit() {
        ObjectNode result = newObject();
        Form<UserRegistInfo> userRegistInfoForm = Form.form(UserRegistInfo.class).bindFromRequest();
        Map<String, String> userMap = userRegistInfoForm.data();
        if (userRegistInfoForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .url(RESET_PASSWORD)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Response response = client.newCall(request).execute();
//                Logger.error(response.toString());
                if (response.isSuccessful()) {
                    JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                    return json;
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
//                Logger.error(json.toString()+"-----"+message.toString());
                return ok(Json.toJson(message));
            });
        }
    }

    /**
     * 个人资料
     *
     * @return views
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> means() {
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder =(Request.Builder)ctx().args.get("request");
            Request request = builder.url(USER_INFO).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                return Json.parse(new String(response.body().bytes(), UTF_8));

            }else  throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<JsonNode , Result>) json -> {
            String path = routes.UserCtrl.myView().url();
            if (session().containsKey("path")) {
                //path = session().get("path");
                session().replace("path", routes.UserCtrl.means().url());
            }else session().put("path", routes.UserCtrl.means().url());
            Message message = Json.fromJson(json.get("message"), Message.class);
            if(null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()){
                Logger.error("message="+(null!=message?message.getCode():0));
                return badRequest();
            }
            UserDTO userInfo = Json.fromJson(json.get("userInfo"), UserDTO.class);
            //请求用户信息
            return ok(views.html.users.means.render(path, userInfo));
        });

    }


    /**
     * 用户昵称
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result nickname() {
        String path = routes.UserCtrl.means().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.nickname().url());
        }else session().put("path", routes.UserCtrl.nickname().url());
        Form<UserDTO> userDTOForm = Form.form(UserDTO.class).bindFromRequest();
        Map<String, String> userMap = userDTOForm.data();
        String nickname = userMap.get("name");
        return ok(views.html.users.nickname.render(path, nickname));
    }

    /**
     * 用户信息修改
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> userUpdate() {
        ObjectNode result = newObject();
        Form<UserDTO> userDTOForm = Form.form(UserDTO.class).bindFromRequest();
        Map<String, String> userMap = userDTOForm.data();
        if (userDTOForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            ObjectNode objectNode = Json.newObject();
            String nickname = null == userMap.get("nickname") ? "" : userMap.get("nickname");
            String gender = null == userMap.get("gender") ? "" : userMap.get("gender");
            String photoUrl = null == userMap.get("photoUrl") ? "" : userMap.get("photoUrl");
            if (!"".equals(nickname)) {
                objectNode.put("nickname", nickname.trim());
            }
            if (!"".equals(gender)) {
                objectNode.put("gender", gender.trim());
            }
            if (!"".equals(photoUrl)) {
                objectNode.put("photoUrl", photoUrl.trim());
            }
            Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
                RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, objectNode.toString());
                Request.Builder builder = (Request.Builder) ctx().args.get("request");
                Request request = builder.url(USER_UPDATE).post(formBody).build();
                Response response = client.newCall(request).execute();
                Logger.error(response.toString());
                if (response.isSuccessful()) {
                    JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                    return json;
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<JsonNode, Result>) json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
//                Logger.error(json.toString()+"-----"+message.toString());
                return ok(Json.toJson(message));
            });
        }

    }

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> userPhoto() {
        play.mvc.Http.MultipartFormData body = request().body().asMultipartFormData();
        play.mvc.Http.MultipartFormData.FilePart picture = body.getFile("picture");
        File file = null;
        String fileName = "";
        if (picture != null) {
            fileName = picture.getFilename();
            String contentType = picture.getContentType();
            file  = picture.getFile();

        } else {
            flash("error", "Missing file");
        }

        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
//        String imgFile = "d:\\111.jpg";//待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(fileName);
            data = new byte[in.available()];
            int read = in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String photoUrl = encoder.encode(data);//返回Base64编码过的字节数组字符串
        Logger.error("photoUrl:"+photoUrl);

        ObjectNode objectNode = Json.newObject();
        objectNode.put("photoUrl", photoUrl);
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, objectNode.toString());
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(USER_UPDATE).post(formBody).build();
            //数据量过大情况下,可以加上这一句
            builder.addHeader("Accept-Encoding","gzip");
            Response response = client.newCall(request).execute();
            Logger.error(response.toString());
            if (response.isSuccessful()) {
                JsonNode json;
                json = Json.parse(new String(response.body().bytes(), UTF_8));
                return json;
            } else throw new IOException("Unexpected code" + response);
        });

        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            Message message = Json.fromJson(json.findValue("message"), Message.class);
                Logger.error(json.toString()+"-----"+message.toString());
            return ok(Json.toJson(message));
        });

    }

    /**
     * 登出
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result logout() {
        //清理cache
        Cache.remove("session_id");
        //清理session
        session().remove("id-token");
        session().remove("session_id");
        session().clear();
        //清理cookie
        return redirect(routes.ProductsCtrl.index());
    }

    /**
     * 关于我们
     *
     * @return
     */
    public Result aboutus() throws IOException {
        String path = routes.UserCtrl.setting().url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.aboutus().url());
        }else session().put("path", routes.UserCtrl.aboutus().url());

        Request request = new Request.Builder()
                .url(VIEWS_ABOUT)
                .build();
        Response response = client.newCall(request).execute();
        String str = new String(response.body().bytes(), UTF_8);
        Logger.error("str:"+str);
        if (response.isSuccessful()) {
            return ok(views.html.users.aboutus.render(path,str));
        } else throw new IOException("Unexpected code " + response);
    }

    //我的拼团
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> mypin() {
        play.libs.F.Promise<JsonNode> promiseOfInt = play.libs.F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(PIN_LIST).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((play.libs.F.Function<JsonNode, Result>) json -> {
         //           Logger.info("===json==" + json);
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                        Logger.error("返回拼团数据错误code=" + (null != message ? message.getCode() : 0));
                        return badRequest(views.html.error500.render());
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    List<PinActivityListDTO> pinList = mapper.readValue(json.get("activityList").toString(), new TypeReference<List<PinActivityListDTO>>() {
                    });
                    for (PinActivityListDTO pin : pinList) {
                        pin.setPinImg(comCtrl.getImgUrl(pin.getPinImg()));
                        pin.setPinSkuUrl(comCtrl.getDetailUrl(pin.getPinSkuUrl()));
                    }


                    return ok(views.html.users.mypin.render(pinList));
                }

        );
    }

    //我的拼团
 //   @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> pinActivity(Long activityId, Integer pay, Integer userPayType) {
        play.libs.F.Promise<JsonNode> promiseOfInt = play.libs.F.Promise.promise(() -> {
            String url="";
            if(userPayType>0){
                url=PIN_ACTIVITY_PAY+activityId+"/"+userPayType;
            }else{
                url=PIN_ACTIVITY + activityId;
            }
           // Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request.Builder builder =comCtrl.getBuilder(request(), session());
            Request request = builder.url(url).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((play.libs.F.Function<JsonNode, Result>) json -> {

            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误code=" + json);
                return badRequest(views.html.error500.render());
            }
            if(message.getCode()!=Message.ErrorCode.SUCCESS.getIndex()){
                 Logger.info("返回数据code=" + json);
                return badRequest(views.html.error.render(message.getMessage()));
            }
            PinActivityDTO pin = Json.fromJson(json.get("activity"), PinActivityDTO.class);
            pin.setPinImg(comCtrl.getImgUrl(pin.getPinImg()));
            pin.setPinSkuUrl(comCtrl.getDetailUrl(pin.getPinSkuUrl()));
            return ok(views.html.shopping.fightgroups.render(pin));
        });
    }

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> pinOrderDetail(Long orderId) {
        play.libs.F.Promise<JsonNode> promiseOfInt = play.libs.F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(PIN_ORDER_DETAIL + orderId).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((play.libs.F.Function<JsonNode, Result>) json -> {
         //   Logger.info("===json==" + json);
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
            List<OrderDTO> orderList = mapper.readValue(json.get("orderList").toString(), new TypeReference<List<OrderDTO>>() {
            });
            if (null == orderList || orderList.isEmpty()) {
                return badRequest();
            }
            if (null != orderList && !orderList.isEmpty()) {
                for (OrderDTO orderDTO : orderList) {
                    for (CartSkuDto sku : orderDTO.getSku()) {
                        sku.setInvImg(comCtrl.getImgUrl(sku.getInvImg()));
                        sku.setInvUrl(comCtrl.getDetailUrl(sku.getInvUrl()));
                    }

                }
            }
            return ok(views.html.users.mypinDetail.render(orderList.get(0)));
        });
    }

    /**
     * 申请售后
     *
     * @return
     */
    public Result service() {
        Form<CartDto> cartDtoForm = Form.form(CartDto.class).bindFromRequest();
        Map<String, String> map = cartDtoForm.data();
        Long orderId = Long.parseLong(map.get("orderId"));
        CartSkuDto cartSkuDto = new CartSkuDto();
        cartSkuDto.setInvImg(map.get("invImg"));
        cartSkuDto.setSkuTitle(map.get("skuTitle"));
        cartSkuDto.setPrice(new BigDecimal(map.get("price")));
        cartSkuDto.setAmount(Integer.parseInt(map.get("amount")));
        cartSkuDto.setSkuId(Long.parseLong(map.get("skuId")));

        String path = routes.ShoppingCtrl.all(orderId).url();
        if (session().containsKey("path")) {
            //path = session().get("path");
            session().replace("path", routes.UserCtrl.service().url());
        } else session().put("path", routes.UserCtrl.service().url());

        return ok(views.html.users.service.render(path,cartSkuDto, orderId));
    }

    /**
     * 售后申请
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> refundApply(){
        ObjectNode result = Json.newObject();
        Form<RefundInfo> refundForm = Form.form(RefundInfo.class).bindFromRequest();
        Logger.info("====refundApply===" + refundForm.data());
        Map<String, String> addressMap = refundForm.data();
        if (refundForm.hasErrors()) { //表单错误
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            //TODO ...

        }

        RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, new String(""));
        return comCtrl.postReqReturnMsg(ORDER_REFUND,formBody);


    }

}

