package controllers;


import com.fasterxml.jackson.databind.JsonNode;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import domain.Item;
import domain.Slider;
import domain.Theme;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static modules.SysParCom.*;
/**
 * 首页,主题,产品相关
 * Created by howen on 16/3/9.
 */
public class ProductsCtrl extends Controller {
    //首页
    public Result index() throws Exception {
        List<Slider> sliderList = new ArrayList<>();
        List<Theme> themeList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(INDEX_PAGE + "1")
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
                        targetUrl = targetUrl.replace(THEME_PAGE,"");
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
                        themeUrl = themeUrl.replace(THEME_PAGE,"");
                        theme.setThemeUrl(themeUrl);
                    }
                    themeList.add(theme);
                }
            }
        }
        return ok(views.html.products.index.render(sliderList,themeList));
    }

    //Ajax加载首页
    public Result loadIndexAjax() throws Exception{
        String pageCount = request().body().asJson().toString();
        List<Theme> themeList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(INDEX_PAGE + pageCount)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            JsonNode json = Json.parse(response.body().string());
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
                        themeUrl = themeUrl.replace(THEME_PAGE,"");
                        theme.setThemeUrl(themeUrl);
                    }
                    themeList.add(theme);
                }
            }
        }
        return ok(Json.toJson(themeList));

    }



    //商品明细
    public Result detail(String type,String url) throws Exception {
        //商品基本信息
        Item itemMain = new Item();
        //商品参数
        List<Object[]> itemFeaturesList = new ArrayList<>();
        //热卖推荐
        List<List<Object[]>> pushResultList = new ArrayList<>();
        //普通商品
        if("D".equals(type) || "item".equals(type) || "vary".equals(type)){
            Request request = new Request.Builder()
                    .url(ITEM_PAGE + url)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonNode json = Json.parse(response.body().string());
                //商品基本信息
                if(json.has("main")){
                    JsonNode mainJson = json.get("main");
                    itemMain = Json.fromJson(mainJson,Item.class);
                    String itemPublicity = itemMain.getPublicity();
                    itemPublicity = itemPublicity.substring(2,itemPublicity.length()-2);
                    itemMain.setPublicity(itemPublicity);
                    //商品参数
                    if(itemMain != null){
                        JsonNode features = Json.parse(itemMain.getItemFeatures());
                        HashMap featuresMap = Json.fromJson(features,HashMap.class);
                        for(Object key : featuresMap.keySet()){
                            Object[] featureObj = new Object[2];
                            featureObj[0] = key;
                            featureObj[1] = featuresMap.get(key);
                            itemFeaturesList.add(featureObj);
                        }
                    }
                }
                //商品Sku
                if(json.has("stock")){
                    JsonNode stockJson = json.get("stock");
                }
                //热卖推荐
                if(json.has("push")){
                    JsonNode pushJson = json.get("push");
                    List<Object[]> pushList = new ArrayList<>();
                    for(JsonNode pushTemp : pushJson){
                        Object[] pushObject = new Object[13];
                        JsonNode imgJson = pushTemp.get("itemImg");
                        String imgStr = Json.fromJson(imgJson,String.class);
                        String imgUrl = Json.fromJson(Json.parse(imgStr).get("url"),String.class);
                        pushObject[0] = imgUrl;
                        String itemType  = Json.fromJson(pushTemp.get("itemType"),String.class);
                        String itemUrl = Json.fromJson(pushTemp.get("itemUrl"),String.class);
                        if("item".equals(itemType) || "vary".equals(itemType)){
                            itemUrl = itemUrl.replace(ITEM_PAGE,"");
                        }
                        if("pin".equals(itemType)){
                            itemUrl = itemUrl.replace(PIN_PAGE,"");
                        }
                        if("customize".equals(itemType)){
                            itemUrl = itemUrl.replace(SUBJECT_PAGE,"");
                        }
                        pushObject[1] = itemUrl;
                        pushObject[2] = Json.fromJson(pushTemp.get("itemTitle"),String.class);
                        pushObject[3] = Json.fromJson(pushTemp.get("itemSrcPrice"),BigDecimal.class);
                        pushObject[4] = Json.fromJson(pushTemp.get("itemPrice"),BigDecimal.class);
                        pushObject[5] = Json.fromJson(pushTemp.get("itemDiscount"),Float.class);
                        pushObject[6] = Json.fromJson(pushTemp.get("itemSoldAmount"),Integer.class);
                        pushObject[7] = Json.fromJson(pushTemp.get("collectCount"),Integer.class);
                        pushObject[8] = Json.fromJson(pushTemp.get("state"),String.class);
                        pushObject[9] = itemType;
                        pushObject[10] = Json.fromJson(pushTemp.get("startAt"),String.class);
                        pushObject[11] = Json.fromJson(pushTemp.get("endAt"),String.class);
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date now = new Date();
                        String strNow = sdfDate.format(now);
                        String endAt = Json.fromJson(pushTemp.get("endAt"),String.class);
                        if(endAt.compareTo(strNow) < 0){
                            pushObject[12] = "已结束";
                        }else{
                            pushObject[12] = "截止" + endAt;
                        }

                        pushList.add(pushObject);
                    }
                    if(pushList.size()%2 == 0){
                        for(int i=0;i<pushList.size()/2;i++){
                            List<Object[]> rowList = new ArrayList<>();
                            rowList.add(pushList.get(i*2));
                            rowList.add(pushList.get(i*2+1));
                            pushResultList.add(rowList);
                        }
                    }else{
                        for(int i=0;i<pushList.size()/2 + 1;i++){
                            List<Object[]> rowList = new ArrayList<>();
                            rowList.add(pushList.get(i*2));
                            rowList.add(pushList.get(i*2+1));
                            pushResultList.add(rowList);
                        }
                    }
                }

            }
        }
        return ok(views.html.products.detail.render(itemMain,itemFeaturesList,pushResultList));
    }

   //拼购详情
   public Result pinDetail() {


        return ok(views.html.products.pinDetail.render());
    }


}
