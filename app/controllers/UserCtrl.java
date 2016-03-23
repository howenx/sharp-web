package controllers;

import akka.actor.ActorSystem;
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
import play.cache.CacheApi;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import scala.concurrent.ExecutionContextExecutor;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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
    private CacheApi cacheApi;

    @Inject
    private ComCtrl comCtrl;

    @Inject
    private FormFactory formFactory;


    //收货地址
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> address() {
        return comCtrl.getReqReturnMsg(false,true,ADDRESS_PAGE).thenApply(json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || (Message.ErrorCode.SUCCESS.getIndex() != message.getCode() && Message.ErrorCode.DATABASE_EXCEPTION.getIndex() != message.getCode())) {
                Logger.error("返回地址数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            //空地址列表
            if (Message.ErrorCode.DATABASE_EXCEPTION.getIndex() == message.getCode()) {
                return ok(views.html.users.addressempty.render());
            } else if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                //地址列表
                ObjectMapper mapper = new ObjectMapper();
                List<Address> addressList = null;
                try {
                    addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ok(views.html.users.address.render(addressList));
            } else return badRequest(views.html.error500.render());
        });
    }

    //创建新的收货地址
    public Result addressnew() {
        return ok(views.html.users.addressnew.render());
    }


    /**
     * 创建新的收货地址
     * @return Result
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> addressSave() {
        ObjectNode result = Json.newObject();
        Form<AddressInfo> addressForm = formFactory.form(AddressInfo.class).bindFromRequest();
        Logger.info("====addressSave===" + addressForm.data());
        Map<String, String> addressMap = addressForm.data();
        String idCardNum = addressMap.get("idCardNum").trim().toLowerCase();
        if (addressForm.hasErrors() || !"".equals(ComTools.IDCardValidate(idCardNum))) { //表单错误或者身份证校验不通过
            Logger.error("收货地址保存表单错误或者身份证校验不通过" + toJson(addressMap));
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        } else {
            ObjectNode object = Json.newObject();
            Long addId = Long.valueOf(addressMap.get("addId"));
            if (addId > 0) {
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
            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, object.toString());
            return comCtrl.postMessageResult(false,true,addId > 0 ? ADDRESS_UPDATE : ADDRESS_ADD,formBody);
        }
    }

    /**
     * 更新地址界面
     *
     * @param addId
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> addressUpdate(Long addId) {

        return comCtrl.getReqReturnMsg(false,true,ADDRESS_PAGE).thenApply(json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回地址数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<Address> addressList = null;
            try {
                addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {
                });
                for (Address address : addressList) {
                    if (address.getAddId() == addId.longValue()) {
                        return ok(views.html.users.addressupdate.render(address));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return badRequest();
        });
    }

    /***
     * 地址删除
     *
     * @return Result
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> addressDel() {
        RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, request().body().asJson().toString());
        return comCtrl.postMessageResult(false,true,ADDRESS_PAGE,formBody);
    }

    //身份认证

    public Result carded() {
        return ok(views.html.users.carded.render());
    }

    //优惠券
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> coupon() {
        return comCtrl.getReqReturnMsg(false,true,COUPON_PAGE).thenApply(json -> {
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CouponVo> couponList = null;
            try {
                couponList = mapper.readValue(json.get("coupons").toString(), new TypeReference<List<CouponVo>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ok(views.html.users.coupon.render(couponList));
        });
    }


    public Result  login() {
        return  ok(views.html.users.login.render(IMAGE_CODE));
    }

    /**
     * 我的界面
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> myView() {
        return comCtrl.getReqReturnMsg(false,true,USER_INFO).thenApply(json -> {
            Logger.info("==myView=json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                return badRequest();
            }
            UserDTO userInfo = Json.fromJson(json.get("userInfo"), UserDTO.class);
            //请求用户信息
            return ok(views.html.users.my.render(userInfo)); //登录了
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
    public CompletionStage<Result> feedback() {
        JsonNode rJson = request().body().asJson();
        String content = "";
        if (rJson.has("content")) {
            content = rJson.findValue("content").asText().trim();
        }
        if ("".equals(content) || content.length() > 140) {
            ObjectNode result = Json.newObject();
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        }
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, rJson.toString());
        return comCtrl.postMessageResult(false,true,FEEDBACK_PAGE, requestBody);
    }

    @Security.Authenticated(UserAuth.class)
    public Result setting() {

        return ok(views.html.users.setting.render());
    }

    /**
     * 我的收藏
     *
     * @return Result
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> collect() {
        return comCtrl.getReqReturnMsg(false,true,COLLECT_PAGE).thenApply(json -> {
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CollectDto> collectList = null;
            try {
                collectList = mapper.readValue(json.get("collectList").toString(), new TypeReference<List<CollectDto>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != collectList && !collectList.isEmpty()) {
                for (CollectDto collectDto : collectList) {
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
     * @return Result
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> collectDel(Long collectId) {
        return comCtrl.getMessageResult(false,true,COLLECT_DEL + collectId);
    }

    /**
     * 收藏
     *
     * @return Result
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> submitCollect() {
        ObjectNode result = newObject();
        RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, request().body().asJson().toString());
        return comCtrl.postReqReturnMsg(false,true,COLLECT_SUBMIT,formBody).thenApply(json -> {
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            Integer collectId = json.get("collectId").asInt();
            result.putPOJO("collectId", collectId);
            return ok(Json.toJson(result));
        });
    }


    /**
     * 用户登录
     *
     * @return Result
     */
    public CompletionStage<Result> loginSubmit() {
        ObjectNode result = newObject();
        Form<UserLoginInfo> userForm = formFactory.form(UserLoginInfo.class).bindFromRequest();
        Map<String, String> userMap = userForm.data();
        if (userForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.completedFuture(ok(result));
        } else {
            FormEncodingBuilder feb = new FormEncodingBuilder();
            userMap.forEach(feb::add);
            RequestBody formBody = feb.build();

            return comCtrl.postReqReturnMsg(false,false,LOGIN_PAGE,formBody).thenApply(json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
                if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                    String token = json.findValue("result").findValue("token").asText();
                    Integer expired = json.findValue("result").findValue("expired").asInt();
                    if (userMap.get("auto").equals("true")) {
                        String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                        cacheApi.set(session_id, token, expired);
                        session().put("session_id", session_id);
                        response().setCookie(Http.Cookie.builder("session_id", session_id).withMaxAge(expired).build());
                        response().setCookie(Http.Cookie.builder("user_token", token).withMaxAge(expired).build());
                    }
                    session("id-token", token);
                }
                return ok(Json.toJson(message));
            });
        }
    }

    /**
     * 快速注册
     *
     * @return views
     */
    public Result registVerify() {
        return ok(views.html.users.registVerify.render());
    }

    /**
     * 手机号检测
     *
     * @return
     */
    public CompletionStage<Result> phoneVerify() {
        ObjectNode result = newObject();
        Form<UserPhoneVerify> userPhoneVerifyForm = formFactory.form(UserPhoneVerify.class).bindFromRequest();
        Map<String, String> userMap = userPhoneVerifyForm.data();
        if (userPhoneVerifyForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        } else {
            CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .url(PHONE_VERIFY)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return Json.parse(new String(response.body().bytes(), UTF_8));
                    } else throw new IOException("Unexpected code" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            return promiseOfInt.thenApply(json -> {
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
    public CompletionStage<Result> phoneCode() {
        ObjectNode result = newObject();
        Form<UserPhoneCode> userPhoneCodeForm = formFactory.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userPhoneCodeForm.data();
        String phone = userMap.get("phone");
        userMap.put("msg", Codecs.md5((phone + "hmm").getBytes()));
        if (userPhoneCodeForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        } else {
            CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .url(PHONE_CODE)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return Json.parse(new String(response.body().bytes(), UTF_8));
                    } else throw new IOException("Unexpected code" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            return promiseOfInt.thenApply(json -> {
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
        Form<UserPhoneCode> userPhoneCodeForm = formFactory.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userPhoneCodeForm.data();
        String phone = userMap.get("phone");
        return ok(views.html.users.signup.render(phone));
    }

    /**
     * 用户注册提交
     *
     * @return
     */
    public CompletionStage<Result> registSubmit() {
        ObjectNode result = newObject();
        Form<UserRegistInfo> userRegistInfoForm = formFactory.form(UserRegistInfo.class).bindFromRequest();
        Map<String, String> userMap = userRegistInfoForm.data();
        if (userRegistInfoForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        } else {
            CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .header("User-Agent", request().getHeader("User-Agent"))
                        .url(REGISTER_PAGE)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return Json.parse(new String(response.body().bytes(), UTF_8));
                    } else throw new IOException("Unexpected code" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            return promiseOfInt.thenApply(json -> {
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
                    Response response2 = null;
                    try {
                        response2 = client.newCall(request2).execute();
                        if (response2.isSuccessful()) {
                            JsonNode json2 = Json.parse(new String(response2.body().bytes(), UTF_8));
                            Message message2 = Json.fromJson(json2.findValue("message"), Message.class);
                            if (Message.ErrorCode.SUCCESS.getIndex() == message2.getCode()) {
                                String token = json.findValue("result").findValue("token").asText();
                                Integer expired = json.findValue("result").findValue("expired").asInt();
                                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                                cacheApi.set(session_id, token, expired);
                                session("session_id", session_id);
                                response().setCookie(Http.Cookie.builder("session_id", session_id).withMaxAge(expired).build());
                                response().setCookie(Http.Cookie.builder("user_token", token).withMaxAge(expired).build());
                                session("id-token", token);
                            }
                            return ok(Json.toJson(message2));
                        } else throw new IOException("Unexpected code" + response2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
        return ok(views.html.users.retrieve.render(IMAGE_CODE));
    }

    /**
     * 密码重置
     *
     * @return
     */
    public Result resetPasswd() {
        Form<UserPhoneCode> userPhoneCodeForm = formFactory.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userPhoneCodeForm.data();
        String phone = userMap.get("phone");
        return ok(views.html.users.resetpassword.render(phone));
    }

    /**
     * 密码修改提交
     *
     * @return
     */
    public CompletionStage<Result> resetPwdSubmit() {
        ObjectNode result = newObject();
        Form<UserRegistInfo> userRegistInfoForm = formFactory.form(UserRegistInfo.class).bindFromRequest();
        Map<String, String> userMap = userRegistInfoForm.data();
        if (userRegistInfoForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        } else {
            CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
                FormEncodingBuilder feb = new FormEncodingBuilder();
                userMap.forEach(feb::add);
                RequestBody formBody = feb.build();
                Request request = new Request.Builder()
                        .url(RESET_PASSWORD)
                        .post(formBody)
                        .build();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return Json.parse(new String(response.body().bytes(), UTF_8));
                    } else throw new IOException("Unexpected code" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Logger.error(response.toString());
                return null;
            });

            return promiseOfInt.thenApply(json -> {
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
    public CompletionStage<Result> means() {
        CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(USER_INFO).get().build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));

                } else throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });

        return promiseOfInt.thenApply(json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("message=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            UserDTO userInfo = Json.fromJson(json.get("userInfo"), UserDTO.class);
            //请求用户信息
            return ok(views.html.users.means.render(userInfo));
        });

    }


    /**
     * 用户昵称
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result nickname() {
        Form<UserDTO> userDTOForm = formFactory.form(UserDTO.class).bindFromRequest();
        Map<String, String> userMap = userDTOForm.data();
        String nickname = userMap.get("name");
        return ok(views.html.users.nickname.render(nickname));
    }

    /**
     * 用户信息修改
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> userUpdate() {
        ObjectNode result = newObject();
        Form<UserDTO> userDTOForm = formFactory.form(UserDTO.class).bindFromRequest();
        Map<String, String> userMap = userDTOForm.data();
        if (userDTOForm.hasErrors()) {
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
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
            Logger.error("userMap:" + userMap);
            Logger.error("objectNode:" + objectNode);
            CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
                RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, objectNode.toString());
                Request.Builder builder = (Request.Builder) ctx().args.get("request");
                Request request = builder.url(USER_UPDATE).post(formBody).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    Logger.error(response.toString());
                    if (response.isSuccessful()) {
                        JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                        return json;
                    } else throw new IOException("Unexpected code" + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            return promiseOfInt.thenApply(json -> {
                Message message = Json.fromJson(json.findValue("message"), Message.class);
                Logger.error(json.toString() + "-----" + message.toString());
                return ok(Json.toJson(message));
            });
        }

    }

    /**
     * 登出
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result logout() {
        //清理cache
        cacheApi.remove("session_id");
        //清理session
        session().remove("id-token");
        session().remove("session_id");
        session().clear();
        //清理cookie
        return redirect(routes.UserCtrl.login());
    }

    //我的拼团
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> mypin() {
        CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(PIN_LIST).get().build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));
                } else throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        return promiseOfInt.thenApply(json -> {
                    Logger.info("===json==" + json);
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                        Logger.error("返回拼团数据错误code=" + (null != message ? message.getCode() : 0));
                        return badRequest(views.html.error500.render());
                    }
                    ObjectMapper mapper = new ObjectMapper();
                    List<PinActivityListDTO> pinList = null;
                    try {
                        pinList = mapper.readValue(json.get("activityList").toString(), new TypeReference<List<PinActivityListDTO>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (PinActivityListDTO pin : pinList) {
                        pin.setPinImg(comCtrl.getImgUrl(pin.getPinImg()));
                        pin.setPinSkuUrl(comCtrl.getDetailUrl(pin.getPinSkuUrl()));
                    }


                    return ok(views.html.users.mypin.render(pinList));
                }

        );
    }

    //申请售后
    public Result service() {
        //请求用户信息
        return ok(views.html.users.service.render());
    }

    //关于我们
    public Result aboutus() {
        //关于我们
        return ok(views.html.users.aboutus.render());
    }


    //我的拼团
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> pinActivity(Long pinActivity) {
        CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(PIN_ACTIVITY + pinActivity).get().build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));
                } else throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        return promiseOfInt.thenApply(json -> {

            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回拼团数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest(views.html.error500.render());
            }
            PinActivityDTO pin = Json.fromJson(json.get("activity"), PinActivityDTO.class);
            pin.setPinImg(comCtrl.getImgUrl(pin.getPinImg()));
            pin.setPinSkuUrl(comCtrl.getDetailUrl(pin.getPinSkuUrl()));
            return ok(views.html.shopping.fightgroups.render(pin));
        });
    }

    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> pinOrderDetail(Long orderId) {
        CompletableFuture<JsonNode> promiseOfInt = CompletableFuture.supplyAsync(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(PIN_ORDER_DETAIL + orderId).get().build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Json.parse(new String(response.body().bytes(), UTF_8));
                } else throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
        return promiseOfInt.thenApply(json -> {
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回拼购订单数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<OrderDTO> orderList = null;
            try {
                orderList = mapper.readValue(json.get("orderList").toString(), new TypeReference<List<OrderDTO>>() {
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * 售后申请
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public CompletionStage<Result> refundApply() {
        ObjectNode result = Json.newObject();
        Form<RefundInfo> refundForm = formFactory.form(RefundInfo.class).bindFromRequest();
        Logger.info("====refundApply===" + refundForm.data());
        Map<String, String> addressMap = refundForm.data();
        if (refundForm.hasErrors()) { //表单错误
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return CompletableFuture.supplyAsync(() -> ok(result));
        } else {
            //TODO ...

        }

        RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, new String(""));
        return comCtrl.postReqReturnMsg(ORDER_REFUND, formBody);


    }

}

