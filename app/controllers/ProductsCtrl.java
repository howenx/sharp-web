package controllers;


import com.fasterxml.jackson.databind.JsonNode;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import domain.Inventory;
import domain.Item;
import domain.Slider;
import domain.Theme;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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

    //主题详情页
    public Result themeDetail(String url) throws Exception {
        String themeImg = "";
        List<Object[]> tagList = new ArrayList<>();
        List<List<Object[]>> itemResultList = new ArrayList<>();
        Request request = new Request.Builder()
                .url(THEME_PAGE + url)
                .build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            JsonNode json = Json.parse(response.body().string());
            if(json.has("themeList")){
                JsonNode themeJson = json.get("themeList");
                //主题主图
                if(themeJson.has("themeImg")){
                    String img = Json.fromJson(themeJson.get("themeImg"),String.class);
                    JsonNode imgJson = Json.parse(img);
                    themeImg = imgJson.get("url").toString();
                    themeImg = themeImg.substring(1,themeImg.length()-1);
                }
                //主题标签
                if(themeJson.has("masterItemTag")){
                    JsonNode tempJson = themeJson.get("masterItemTag");
                    String tags = Json.fromJson(tempJson,String.class);
                    if(tags != null){
                        JsonNode tagJson = Json.parse(tags);
                        for(JsonNode tag : tagJson){
                            Logger.error(tag.toString());
                            Object[] tagObject = new Object[6];
                            tagObject[0] = tag.get("top").asDouble() * 100;
                            String tagUrl = Json.fromJson(tag.get("url"),String.class);
                            String urlType = "";
                            if(tagUrl.contains(PIN_PAGE)){
                                tagUrl = tagUrl.replace(PIN_PAGE,"");
                                urlType = "pin";
                            }
                            if(tagUrl.contains(ITEM_PAGE)){
                                tagUrl = tagUrl.replace(ITEM_PAGE,"");
                                urlType = "item";
                            }
                            tagObject[1] = tagUrl;
                            tagObject[2] = tag.get("left").asDouble() * 100;
                            String tagName = tag.get("name").toString();
                            tagName = tagName.substring(1,tagName.length()-1);
                            tagObject[3] = tagName;
                            tagObject[4] = tag.get("angle").asInt();
                            tagObject[5] = urlType;
                            tagList.add(tagObject);
                        }

                    }
                }
                //主题中的商品
                if(themeJson.has("themeItemList")){
                    JsonNode itemJson = themeJson.get("themeItemList");
                    List<Object[]> itemList = new ArrayList<>();
                    for(JsonNode tempJson : itemJson){
                        Object[] itemObject = new Object[8];
                        String itemImg = Json.fromJson(tempJson.get("itemImg"),String.class);
                        JsonNode itemImgJson = Json.parse(itemImg);
                        String itemImgUrl = itemImgJson.get("url").toString();
                        itemImgUrl = itemImgUrl.substring(1,itemImgUrl.length()-1);
                        itemObject[0] = itemImgUrl;                                         //商品图片
                        String itemType = Json.fromJson(tempJson.get("itemType"),String.class);
                        String itemUrl = Json.fromJson(tempJson.get("itemUrl"),String.class);
                        if("pin".equals(itemType)){
                            itemUrl = itemUrl.replace(PIN_PAGE,"");
                        }
                        if("item".equals(itemType) || "vary".equals(itemType)){
                            itemUrl = itemUrl.replace(ITEM_PAGE,"");
                        }
                        if("customize".equals(itemType)){
                            itemUrl = itemUrl.replace(SUBJECT_PAGE,"");
                        }
                        itemObject[1] = itemType;                                                   //商品类型
                        itemObject[2] = itemUrl;                                                    //商品链接
                        itemObject[3] = Json.fromJson(tempJson.get("itemTitle"),String.class);      //商品Title
                        itemObject[4] = Json.fromJson(tempJson.get("itemSrcPrice"),String.class);   //商品原价
                        itemObject[5] = Json.fromJson(tempJson.get("itemPrice"),String.class);      //商品价格
                        itemObject[6] = Json.fromJson(tempJson.get("itemDiscount"),String.class);   //商品折扣
                        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date now = new Date();
                        String strNow = sdfDate.format(now);
                        String endAt = Json.fromJson(tempJson.get("endAt"),String.class);
                        Date endAtDate = sdfDate.parse(endAt);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(endAtDate);
                        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                        if(calendar.get(Calendar.HOUR_OF_DAY)<10){
                             hour = "0" + hour;
                        }
                        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                        if(calendar.get(Calendar.MINUTE)<10){
                            minute = "0" + minute;
                        }
                        String endDate =(calendar.get(Calendar.MONTH)+1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + hour +":"+minute;
                        if(endAt.compareTo(strNow) < 0){
                            itemObject[7] = "已结束";
                        }else{
                            itemObject[7] = "截止" + endDate;
                        }
                        itemList.add(itemObject);
                    }
                    for(int i=0;i<itemList.size()/2;i++){
                        List<Object[]> rowList = new ArrayList<>();
                        rowList.add(itemList.get(i*2));
                        rowList.add(itemList.get(i*2+1));
                        itemResultList.add(rowList);
                    }
                    if(itemList.size()%2 != 0){
                        List<Object[]> rowList = new ArrayList<>();
                        rowList.add(itemList.get(itemList.size()-1));
                        itemResultList.add(rowList);
                    }
                }
            }
        }
        return ok(views.html.products.themeDetail.render(themeImg,tagList,itemResultList));
    }


    //商品明细
    public Result detail(String type,String url) throws Exception {
        //商品基本信息
        Item itemMain = new Item();
        //商品参数
        List<Object[]> itemFeaturesList = new ArrayList<>();
        //热卖推荐
        List<List<Object[]>> pushResultList = new ArrayList<>();
        //商品Sku
        List<Inventory> inventoryList = new ArrayList<>();
        //Sku商品预览图片
        List<List<String>> preImgList = new ArrayList<>();

        //普通商品
        if("D".equals(type) || "item".equals(type) || "vary".equals(type) || "customize".equals(type)){
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
                    for(JsonNode stockInv : stockJson){
                        Inventory inventory = Json.fromJson(stockInv,Inventory.class);
                        JsonNode imgJson = Json.parse(inventory.getInvImg());
                        String imgUrl = Json.fromJson(imgJson.get("url"),String.class);
                        inventory.setInvImg(imgUrl);
                        JsonNode previewImgJson = Json.parse(inventory.getItemPreviewImgs());
                        List<String> preImgSubList = new ArrayList<>();
                        for(JsonNode previewJson : previewImgJson){
                            preImgSubList.add(Json.fromJson(previewJson.get("url"),String.class));
                        }
                        preImgList.add(preImgSubList);
                        inventoryList.add(inventory);
                    }

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
                        Date endAtDate = sdfDate.parse(endAt);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(endAtDate);
                        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                        if(calendar.get(Calendar.HOUR_OF_DAY)<10){
                            hour = "0" + hour;
                        }
                        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                        if(calendar.get(Calendar.MINUTE)<10){
                            minute = "0" + minute;
                        }
                        String endDate =(calendar.get(Calendar.MONTH)+1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + hour +":"+minute;

                        if(endAt.compareTo(strNow) < 0){
                            pushObject[12] = "已结束";
                        }else{
                            pushObject[12] = "截止" + endDate;
                        }

                        pushList.add(pushObject);
                    }

                    for(int i=0;i<pushList.size()/2;i++){
                        List<Object[]> rowList = new ArrayList<>();
                        rowList.add(pushList.get(i*2));
                        rowList.add(pushList.get(i*2+1));
                        pushResultList.add(rowList);
                    }
                    if(pushList.size()%2 != 0){
                        List<Object[]> rowList = new ArrayList<>();
                        rowList.add(pushList.get(pushList.size()-1));
                        pushResultList.add(rowList);
                    }
                }
            }
            return ok(views.html.products.detail.render(itemMain,itemFeaturesList,pushResultList,inventoryList,inventoryList.size(),preImgList));
        }
        //拼购商品
        else{
            Request request = new Request.Builder()
                    .url(PIN_PAGE + url)
                    .build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                JsonNode json = Json.parse(response.body().string());
                //拼购商品基本信息
                if(json.has("main")){
                    JsonNode mainJson = json.get("main");
                    itemMain = Json.fromJson(mainJson,Item.class);
//                    String itemPublicity = itemMain.getPublicity();
//                    itemPublicity = itemPublicity.substring(2,itemPublicity.length()-2);
//                    itemMain.setPublicity(itemPublicity);
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
                    Logger.error(itemMain.toString());
                }
                //拼购商品Sku
                JsonNode stockJson = json.get("stock");

                //商品推荐
                JsonNode pushJson = json.get("push");
            }
            return ok(views.html.products.pinDetail.render(itemMain,itemFeaturesList));
        }
    }
}
