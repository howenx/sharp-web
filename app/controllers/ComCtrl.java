package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import filters.UserAuth;
import modules.SysParCom;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.Crypto;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import static java.nio.charset.StandardCharsets.UTF_8;
import static modules.SysParCom.*;
import static play.libs.Json.toJson;

/**
 * Created by sibyl.sun on 16/3/18.
 */
public class ComCtrl extends Controller {

    @Inject
    WSClient ws;
    /**
     * 图片json串转图片url
     * @param imgJson
     * @return
     */
    public String getImgUrl(String imgJson){
        if (imgJson.contains("url")) {
            JsonNode jsonNode = Json.parse(imgJson);
            if (jsonNode.has("url")) {
                return jsonNode.get("url").asText();
            }
        }
        return SysParCom.IMAGE_URL + imgJson;
    }

    /**
     * 获取商品详情的URL
     * @param oldUrl
     * @return
     */
    public String getDetailUrl(String oldUrl){
//        Logger.info(oldUrl+"===="+oldUrl.indexOf("/detail/")+"==="+oldUrl.substring(oldUrl.indexOf("/detail/")));
        if(oldUrl.indexOf("/detail/")<0){
            return oldUrl;
        }
        return oldUrl.substring(oldUrl.indexOf("/detail/"));
//        Logger.info(oldUrl+"======="+skuType+"==="+PIN_PAGE);
//        if("pin".equals(skuType)){
//            return oldUrl.replace(PIN_PAGE,"");
//        }
//        if("item".equals(skuType)){
//            return oldUrl.replace(ITEM_PAGE,"");
//        }
//        if("vary".equals(skuType)){
//            return oldUrl.replace(VARY_PAGE,"");
//        }
//        if("customize".equals(skuType)){
//            return oldUrl.replace(CUSTOMIZE_PAGE,"");
//        }
//       return oldUrl;
    }

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> getReqReturnMsg(String url){
        return sendReq(url,null);

    }
    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> postReqReturnMsg(String url,RequestBody requestBody){
        return sendReq(url,requestBody);

    }
    @Security.Authenticated(UserAuth.class)
    private F.Promise<Result> sendReq(String url,RequestBody requestBody){
        F.Promise<JsonNode> promiseOfInt = F.Promise.promise(() -> {
            Request.Builder builder = (Request.Builder) ctx().args.get("request");
            Request request =null;
            if(null==requestBody){
                request= builder.url(url).get().build();
            }else{
                request= builder.url(url).post(requestBody).build();
            }
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return Json.parse(new String(response.body().bytes(), UTF_8));
            } else throw new IOException("Unexpected code " + response);
        });
        return promiseOfInt.map((F.Function<JsonNode, Result>) json -> {
         //   Logger.info(url+"返回结果---->\n"+json);
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message) {
                Logger.error("返回数据错误json=" + json);
                return badRequest();
            }
            return ok(toJson(message));
        } );
    }

    /**
     * 订单加密
     * @param orderId
     * @param token
     * @return
     */
    public String orderSecurityCode(String orderId,String token){
        Map<String,String> map = new TreeMap<>();
        map.put("orderId",orderId);
        map.put("token",token);
        try {
            return Crypto.getSignature(map,"HMM");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    public Result redirectWeixin(String code,String state){

        ws.url(SysParCom.WEIXIN_ACCESS+"appid="+WEIXIN_APPID+"&secret="+WEIXIN_SECRET+"&code="+code+"&grant_type=authorization_code").get().map(wsResponse -> {
            JsonNode response = wsResponse.asJson();

            Logger.info("微信换取Access_token返回的数据JSON: " + response.toString());

            ws.url(SysParCom.WEIXIN_ACCESS+"appid="+WEIXIN_APPID+"&secret="+WEIXIN_SECRET+"&code="+code+"&grant_type=authorization_code");
            return null;
        });
        return null;
    }

    public Request.Builder getBuilder(Http.Request request, Http.Session session) {

        Request.Builder builder = new Request.Builder();
        builder.addHeader(Http.HeaderNames.X_FORWARDED_FOR,request.remoteAddress());
        builder.addHeader(Http.HeaderNames.VIA,request.remoteAddress());
        builder.addHeader("User-Agent",request.getHeader("User-Agent"));
        if (session.containsKey("id-token")) {
            builder.addHeader("id-token", session.get("id-token"));
        }
        return builder;
    }
}
