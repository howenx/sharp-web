package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import domain.Message;
import modules.SysParCom;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static modules.SysParCom.client;
import static util.GZipper.dealToString;

/**
 * 公共调用
 * Created by sibyl.sun on 16/3/18.
 */
public class ComCtrl extends Controller {

    /**
     * 图片json串转图片url
     *
     * @param imgJson imgJson
     * @return String
     */
    public String getImgUrl(String imgJson) {
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
     *
     * @param oldUrl oldUrl
     * @return String
     */
    public String getDetailUrl(String oldUrl) {
        if (!oldUrl.contains("/detail/")) {
            return oldUrl;
        }
        return oldUrl.substring(oldUrl.indexOf("/detail/"));
    }

    /**
     * get请求返回JsonNode方便处理
     * @see controllers.UserCtrl#collect 调用参考
     * @param gzip 是否使用gzip压缩数据传输方式
     * @param auth 是否需要校验用户有未登录
     * @param url 请求地址
     * @return JsonNode
     */
    public CompletionStage<JsonNode> getReqReturnMsg(Boolean gzip, Boolean auth, String url) {
        return sendReq(gzip, auth, url, null);

    }

    /**
     * post请求返回JsonNode方便处理
     * @see controllers.UserCtrl#loginSubmit 调用参考
     * @param gzip 是否使用gzip压缩数据传输方式
     * @param auth 是否需要校验用户有未登录
     * @param url 请求地址
     * @param requestBody 需要提交表单的数据
     * @return JsonNode
     */
    public CompletionStage<JsonNode> postReqReturnMsg(Boolean gzip, Boolean auth, String url, RequestBody requestBody) {
        return sendReq(gzip, auth, url, requestBody);

    }

    /**
     * get请求直接返回result,在API接口只返回Message时使用,此时对返回数据不做其它处理时使用
     * @see controllers.UserCtrl#collectDel 调用参考
     * @param gzip 是否使用gzip压缩数据传输方式
     * @param auth 是否需要校验用户有未登录
     * @param url 请求地址
     * @return Result
     */
    public CompletionStage<Result> getMessageResult(Boolean gzip, Boolean auth, String url){
        return getReqReturnMsg(gzip,auth,url).thenApply(json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            return ok(json);
        });
    }

    /**
     * post请求直接返回result,在API接口只返回Message时使用,此时对返回数据不做其它处理时使用
     * @see controllers.UserCtrl#addressSave 调用参考
     * @param gzip 是否使用gzip压缩数据传输方式
     * @param auth 是否需要校验用户有未登录
     * @param url 请求地址
     * @param requestBody 需要提交表单的数据
     * @return Result
     */
    public CompletionStage<Result> postMessageResult(Boolean gzip, Boolean auth, String url, RequestBody requestBody){
        return postReqReturnMsg(gzip,auth,url, requestBody).thenApply(json -> {
            Message message = Json.fromJson(json.get("message"), Message.class);
            if (null == message || message.getCode() != Message.ErrorCode.SUCCESS.getIndex()) {
                Logger.error("返回收藏数据错误code=" + (null != message ? message.getCode() : 0));
                return badRequest();
            }
            return ok(json);
        });
    }

    private Request.Builder getBuilder(Boolean auth) {
        if (auth) {
            return (Request.Builder) ctx().args.get("request");
        } else {
            Map<String, String[]> mapString = request().headers();
            Map<String, String> params = new HashMap<>();
            if (mapString != null) {
                mapString.forEach((k, v) -> params.put(k, v[0]));
            }
            params.remove("Accept-Encoding");
            Request.Builder builder = new Request.Builder();
            params.forEach(builder::addHeader);
            if (session().containsKey("id-token")) {
                builder.addHeader("id-token", session().get("id-token"));
            }
            return builder;
        }
    }

    private CompletionStage<JsonNode> sendReq(Boolean gzip, Boolean auth, String url, RequestBody requestBody) {
        Request.Builder builder = getBuilder(auth);
        if (gzip) builder.addHeader("Accept-Encoding", "gzip");
        return CompletableFuture.supplyAsync(() -> {
            Request request = null;
            if (null == requestBody) {
                request = builder.url(url).get().build();
            } else {
                request = builder.url(url).post(requestBody).build();
            }
            try {
                Response response  = client.newCall(request).execute();
                client.setConnectTimeout(10, TimeUnit.SECONDS);
                if (response.isSuccessful()) {
                    String result = dealToString(response);
                    Logger.debug("打印返回结果数据------->" + result);
                    if (result != null) {
                        return Json.parse(result);
                    } else throw new IOException("Unexpected code" + response);
                } else throw new IOException("Unexpected code " + response);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
