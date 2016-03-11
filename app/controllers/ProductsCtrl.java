package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.Slider;
import domain.Theme;
import filters.UserAuth;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static modules.SysParCom.*;
/**
 * 首页,主题,产品相关
 * Created by howen on 16/3/9.
 */
public class ProductsCtrl extends Controller {


    //首页
    public Result index() {
        Request request = new Request.Builder()
                .url(INDEX_PAGE)
                .build();

            client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Response response) throws IOException {
                JsonNode json = Json.parse(response.body().string());
                List<Slider> sliderList = new ArrayList<Slider>();
                List<Theme> themeList = new ArrayList<Theme>();
                if(json.has("slider")){
                    JsonNode sliderJson = json.get("slider");
                    for(JsonNode sliderTemp : sliderJson){
                        Slider slider = Json.fromJson(sliderTemp,Slider.class);
                        sliderList.add(slider);
                    }
                }
                if(json.has("theme")){
                    JsonNode themeJson = json.get("theme");
                    for(JsonNode themeTemp : themeJson){
                        Theme theme = Json.fromJson(themeTemp,Theme.class);
                        themeList.add(theme);
                    }
                }
                Logger.error(response.toString());
                Logger.error(sliderList.toString());
                Logger.error(themeList.toString());
            }
        });
        return ok(views.html.products.index.render());
    }


    //商品明细
    public Result detail() {
        return ok(views.html.products.detail.render());
    }


}
