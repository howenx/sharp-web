package controllers;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import play.Logger;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static modules.SysParCom.*;
import static modules.SysParCom.client;
import static util.GZipper.dealToString;

/**
 * 广告
 * Created by sibyl.sun on 16/9/2.
 */
public class AdCtrl extends Controller {

    @Inject
    ComCtrl comCtrl;

    /**
     * 接收亿起发参数接口
     * @return
     */
    public Result track(){
        Map<String, String[]> body_map = request().queryString();
        Map<String, String> params = new HashMap<>();
        body_map.forEach((k, v) -> params.put(k, v[0]));
        Logger.info("亿起发参数\nparams="+params);
        int expires=YIQIFA_COOKIE_EXPIRES*24*60*60;
        if(null!=params.get("cid")){
            ctx().response().setCookie("cid", params.get("cid"), expires);
        }
        if(null!=params.get("channel")){
            ctx().response().setCookie("channel", params.get("channel"), expires);
        }
        if(null!=params.get("aid")){
            ctx().response().setCookie("aid", params.get("aid"), expires);
        }
        if(null!=params.get("wi")){
            ctx().response().setCookie("wi", params.get("wi"), expires);
        }
        String url=null;
        if(null!=params.get("url")){
            url=params.get("url");
        }
        if(null==url||"".equals(url)){
            url=M_HTTP;
        }
        return redirect(url);

    }

    /**
     * 亿起发查询订单
     * @return
     */
    public F.Promise<Result> yiqifaQueryorder(){
        Map<String, String[]> body_map = request().queryString();
        Map<String, String> params = new HashMap<>();
        body_map.forEach((k, v) -> params.put(k, v[0]));
     //   Logger.info("亿起发查询订单参数\nparams="+params);
        String date="";
        if(null!=params.get("d")){
            date=params.get("d");
        }
        if(null!=params.get("ud")){
            date=params.get("ud");
        }

        try {
            //校验时间格式
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");
            simpleDateFormat.setLenient(false);
            simpleDateFormat.parse(date);
        } catch (Exception e) {
            return F.Promise.promise((F.Function0<Result>) () -> ok("FAIL,时间格式不是yyyyMMdd"));
        }
        if(null==params.get("cid")||"".equals(params.get("cid"))||"".equals(date)){
           return F.Promise.promise((F.Function0<Result>) () -> ok("FAIL,cid或者date为空"));
        }
        final String finalDate = date;
        F.Promise<String> promise = F.Promise.promise(() -> {
            Request request =comCtrl.getBuilder(ctx())
                    .url(AD_QUERY_ORDER +YIQIFA_AID+"/"+params.get("cid")+"/"+ finalDate)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return result;
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });

        return promise.map((F.Function<String, Result>) re -> {
            return ok(re);
        });
    }


    /**
     * ios通配符 apple-app-site-association
     * @return
     */
    public Result appleAppSiteAssociation(){
        return ok("{\n" +
                "\"applinks\": {\n" +
                "\"apps\": [],\n" +
                "\"details\": [\n" +
                "{\n" +
                "\"appID\": \"6E6255HJ5K.com.kakao.kakaogift\",\n" +
                "\"paths\": [ \"*\" ]\n" +
                "}\n" +
                "]\n" +
                "}\n" +
                "}");
    }
}
