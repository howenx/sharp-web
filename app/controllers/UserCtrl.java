package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.*;
import domain.Address;
import domain.*;
import filters.UserAuth;
import modules.ComTools;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
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

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
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

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    //收货地址
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> address() {
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(ADDRESS_PAGE).get().build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            Logger.error("返回---->\n" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回地址数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<Address> addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {
            });
            return ok(views.html.users.address.render(addressList));
        });
    }

    //创建新的收货地址
    public Result addressnew() {
        return ok(views.html.users.addressnew.render());
    }

    //创建新的收货地址
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> addressSave() {

        ObjectNode result = Json.newObject();
        Form<AddressInfo> addressForm = Form.form(AddressInfo.class).bindFromRequest();
        Logger.info("====addressSave===" + addressForm.data());
        Map<String, String> addressMap = addressForm.data();
        String idCardNum = addressMap.get("idCardNum").trim();
        if (addressForm.hasErrors() || !"".equals(ComTools.IDCardValidate(idCardNum.toLowerCase()))) { //表单错误或者身份证校验不通过
            result.putPOJO("message", Json.toJson(new Message(Message.ErrorCode.getName(Message.ErrorCode.BAD_PARAMETER.getIndex()), Message.ErrorCode.BAD_PARAMETER.getIndex())));
            return Promise.promise((Function0<Result>) () -> ok(result));
        } else {
            ObjectNode object = Json.newObject();
            Long addId = Long.valueOf(addressMap.get("addId"));
            object.put("addId", addId);
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
            object.put("idCardNum", addressMap.get("idCardNum").trim());


            Promise<Message> promiseOfInt = Promise.promise(() -> {
                RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, new String(object.toString()));
                Request.Builder builder = (Request.Builder) ctx().args.get("request");
                Request request = builder.url(addId > 0 ? ADDRESS_UPDATE : ADDRESS_ADD).post(formBody).build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    JsonNode json = Json.parse(new String(response.body().bytes(), UTF_8));
                    Logger.info("===json==" + json);
                    Message message = Json.fromJson(json.get("message"), Message.class);
                    if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                        Logger.error("返回创建新的收货地址数据错误code=" + (null != message ? message.getCode() : 0));
                    }
                    return message;
                } else throw new IOException("Unexpected code" + response);
            });

            return promiseOfInt.map((Function<Message, Result>) pi -> {
                Logger.error("返回结果" + pi);
                return ok(toJson(pi));
            });
        }
    }

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> addressUpdate(Long addId) {
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(ADDRESS_PAGE).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            Logger.error("返回---->\n" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回地址数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<Address> addressList = mapper.readValue(json.get("address").toString(), new TypeReference<List<Address>>() {
            });
            for (Address address : addressList) {
                if (address.getAddId() == addId.longValue()) {
                    return ok(views.html.users.addressupdate.render(address));
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
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            RequestBody formBody = RequestBody.create(MEDIA_TYPE_JSON, new String(request().body().asJson().toString()));
            Request request = builder.url(ADDRESS_DEL).post(formBody).build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });

        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            Logger.error("返回---->\n" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回地址删除数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            return ok(toJson(message));
        });
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
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CouponVo> couponList = mapper.readValue(json.get("coupons").toString(), new TypeReference<List<CouponVo>>() {
            });
            return ok(views.html.users.coupon.render(couponList));
        });
    }


    public Result login() {
        return ok(views.html.users.login.render(IMAGE_CODE));
    }

    public Result myView() {
        return ok(views.html.users.my.render());
    }

    public Result tickling() {
        return ok(views.html.users.tickling.render());
    }

    @Security.Authenticated(UserAuth.class)
    public Result setting() {
        Request.Builder builder = (Request.Builder) ctx().args.get("request");

        Logger.error("session token----> " + session().get("id-token"));
        Logger.error("Cache user----> " + cache.get(session().get("id-token")));
        Logger.error(request().cookie("user_token").value());

        return ok(views.html.users.setting.render());
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
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<CollectDto> collectList = mapper.readValue(json.get("collectList").toString(), new TypeReference<List<CollectDto>>() {
            });
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
        Promise<JsonNode> promiseOfInt = Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(COLLECT_DEL + collectId).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));


            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((Function<JsonNode, Result>) json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回取消收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            return ok(toJson(message));
        });
    }


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
                        String token = json.findValue("result").findValue("token").asText();
                        Integer expired = json.findValue("result").findValue("expired").asInt();
                        if (Message.ErrorCode.SUCCESS.getIndex() == message.getCode()) {
                            if (userMap.get("auto").equals("true")) {
                                String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                                Cache.set(session_id, token, expired);
                                session("session_id", session_id);
                                response().setCookie("session_id", session_id, expired);
                                response().setCookie("user_token", token, expired);
                            }
                            session("id-token", token);
                        }
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
        return ok(views.html.users.registVerify.render());
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
        Form<UserPhoneCode> userRegistCodeForm = Form.form(UserPhoneCode.class).bindFromRequest();
        Map<String, String> userMap = userRegistCodeForm.data();
        if (userRegistCodeForm.hasErrors()) {
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
     * @param phone
     * @return views
     */
    public Result regist(String phone) {
        //String phone = request().body().asJson().toString();
        return ok(views.html.users.regist.render(phone));
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
                            String session_id = UUID.randomUUID().toString().replaceAll("-", "");
                            Cache.set(session_id, json2.findValue("token").asText(), json2.findValue("expired").asInt());
                            session("session_id", session_id);
                            response().setCookie("session_id", session_id, json2.findValue("expired").asInt());
                            response().setCookie("user_token", json2.findValue("token").asText(), json2.findValue("expired").asInt());
                            session("id-token", json2.findValue("token").asText());
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
        return ok(views.html.users.retrieve.render(IMAGE_CODE));
    }

    /**
     * 密码重置
     *
     * @param phone
     * @return
     */
    public Result resetPasswd(String phone) {
        return ok(views.html.users.resetPasswd.render(phone));
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

    /**
     * 个人资料
     *
     * @return views
     */
    @Security.Authenticated(UserAuth.class)
    public Result means() {
        return ok(views.html.users.means.render());
    }


    /**
     * 用户昵称
     *
     * @return
     */
    @Security.Authenticated(UserAuth.class)
    public Result nickname() {
        String nickname = request().body().asJson().toString();
        Logger.error(nickname);
        return ok("success");
        //return ok(views.html.users.nickname.render(nickname));
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
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回拼团数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest(views.html.error500.render());
            }
            ObjectMapper mapper = new ObjectMapper();
            List<PinActivityListDTO> pinList = mapper.readValue(json.get("activityList").toString(), new TypeReference<List<PinActivityListDTO>>() {
            });
            for (PinActivityListDTO pin : pinList) {
                if (pin.getPinImg().contains("url")) {
                    JsonNode jsonNode = Json.parse(pin.getPinImg());
                    if (jsonNode.has("url")) {
                        pin.setPinImg(jsonNode.get("url").asText());
                    }
                } else
                    pin.setPinImg(SysParCom.IMAGE_URL + pin.getPinImg());
            }

            return ok(views.html.users.mypin.render(pinList));
        });
    }


    //我的拼团
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> pinActivity(Long pinActivity) {
        play.libs.F.Promise<JsonNode> promiseOfInt = play.libs.F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request = builder.url(PIN_ACTIVITY + pinActivity).get().build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((play.libs.F.Function<JsonNode, Result>) json -> {
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回拼团数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest(views.html.error500.render());
            }
            ObjectMapper mapper = new ObjectMapper();
            PinActivityDTO pin = Json.fromJson(json.get("activity"), PinActivityDTO.class);
            if (pin.getPinImg().contains("url")) {
                JsonNode jsonNode = Json.parse(pin.getPinImg());
                if (jsonNode.has("url")) {
                    pin.setPinImg(jsonNode.get("url").asText());
                }
            } else
                pin.setPinImg(SysParCom.IMAGE_URL + pin.getPinImg());

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
            Logger.info("===json==" + json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回拼购订单数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            ObjectMapper mapper = new ObjectMapper();
            List<OrderDTO> orderList = mapper.readValue(json.get("orderList").toString(), new TypeReference<List<OrderDTO>>() {
            });
            if (null == orderList || orderList.isEmpty()) {
                return badRequest();
            }
            return ok(views.html.users.mypinDetail.render(orderList.get(0)));
        });
    }

}

