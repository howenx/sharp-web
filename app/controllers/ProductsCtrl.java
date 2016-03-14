package controllers;


import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.Slider;
import domain.Theme;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import java.util.ArrayList;
import java.util.List;

import static modules.SysParCom.*;
/**
 * 首页,主题,产品相关
 * Created by howen on 16/3/9.
 */
public class ProductsCtrl extends Controller {
    //首页
    public Result index() throws Exception {
        List<Slider> sliderList = new ArrayList<Slider>();
        List<Theme> themeList = new ArrayList<Theme>();
        Request request = new Request.Builder()
                .url(INDEX_PAGE)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            JsonNode json = Json.parse(response.body().string());
            if(json.has("slider")){
                JsonNode sliderJson = json.get("slider");
                for(JsonNode sliderTemp : sliderJson){
                    Slider slider = Json.fromJson(sliderTemp,Slider.class);
                    JsonNode urlJson = Json.parse(slider.getUrl());
                    String img = urlJson.get("url").toString();
                    img = img.substring(1,img.length()-1);
                    slider.setImg(img);
                    String targetUrl = slider.getItemTarget();
                    //主题
                    if("T".equals(slider.getTargetType())){
                        //targetUrl = targetUrl.replace("http://172.28.3.51:9001/topic/list/","");
                        targetUrl = targetUrl.replace(IHEME_PAGE,"");
                    }
                    //普通商品
                    if("D".equals(slider.getTargetType())){
                        //targetUrl = targetUrl.replace("http://172.28.3.51:9001/comm/detail/","");
                        targetUrl = targetUrl.replace(ITEM_PAGE,"");
                    }
                    //拼购商品
                    if("P".equals(slider.getTargetType())){
                        //targetUrl = targetUrl.replace("http://172.28.3.51:9001/comm/pin/detail/","");
                        targetUrl = targetUrl.replace(PIN_PAGE,"");
                    }
                    slider.setItemTarget(targetUrl);
                    sliderList.add(slider);
                }
            }
            if(json.has("theme")){
                JsonNode themeJson = json.get("theme");
                for(JsonNode themeTemp : themeJson){
                    Theme theme = Json.fromJson(themeTemp,Theme.class);
                    JsonNode imgJson = Json.parse(theme.getThemeImg());
                    String imgUrl = imgJson.get("url").toString();
                    imgUrl = imgUrl.substring(1,imgUrl.length()-1);
                    theme.setThemeImg(imgUrl);
                    if(!"h5".equals(theme.getType())){
                        String themeUrl = theme.getThemeUrl();
                        //themeUrl = themeUrl.replace("http://172.28.3.51:9001/topic/list/","");
                        themeUrl = themeUrl.replace(IHEME_PAGE,"");
                        theme.setThemeUrl(themeUrl);
                    }
                    themeList.add(theme);
                }
            }
        }
        return ok(views.html.products.index.render(sliderList,themeList));
    }


    //商品明细
    public Result detail(String type,String url) {


        return ok(views.html.products.detail.render());
    }

    //拼购详情
   public Result pinDetail() {


        return ok(views.html.products.pinDetail.render());
    }


}
