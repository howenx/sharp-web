package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import domain.*;
import play.Logger;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static modules.SysParCom.*;
import static util.GZipper.dealToString;

/**
 * 首页,主题,产品相关
 * Created by howen on 16/3/9.
 */
public class ProductsCtrl extends Controller {
    @Inject
    ComCtrl comCtrl;


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

    /**
     * 首页
     *
     * @return
     * @throws Exception
     */
    public F.Promise<Result> index(){
        comCtrl.pushOrPopHistoryUrl(ctx());
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            //Request request = comCtrl.getBuilder(ctx())
            Request request = comCtrl.getBuilder(ctx())
                    .url(INDEX_PAGE + "1")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map(json -> {
            if(LOG_OPEN){
                Logger.info("index接收数据-->\n"+json);
            }
            List<Slider> sliderList = new ArrayList<>();
            List<SliderNav> sliderNavList = new ArrayList<>();
            List<Theme> themeList = new ArrayList<>();
            int pageCount = json.get("page_count").asInt();
            if (json.has("slider")) {
                JsonNode sliderJson = json.get("slider");
                for (JsonNode sliderTemp : sliderJson) {
                    try {
                        Slider slider = Json.fromJson(sliderTemp, Slider.class);
//                    if(null!=slider.getUrl()){
//                        JsonNode imgJson = Json.parse(slider.getUrl());
//                        slider.setImg(imgJson.get("url").asText());
//                    }
                        slider.setImg(slider.getUrl());

                        //处理TDPU类型的目标地址在M端应该转化成的最终地址
                        slider.setItemTarget(comCtrl.getFinalItemTargetByTypeTDPU(slider.getTargetType(), slider.getItemTarget()));

                        sliderList.add(slider);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            if (json.has("sliderNav")) {
                JsonNode sliderJson = json.get("sliderNav");
                for (JsonNode sliderTemp : sliderJson) {
                    try {
                        SliderNav slider = Json.fromJson(sliderTemp, SliderNav.class);
//                    if(null!=slider.getUrl()){
//                        JsonNode imgJson = Json.parse(slider.getUrl());
//                        slider.setImg(imgJson.get("url").asText());
//                    }
                        slider.setImg(slider.getUrl());

                        //处理TDPU类型的目标地址在M端应该转化成的最终地址
                        slider.setItemTarget(comCtrl.getFinalItemTargetByTypeTDPU(slider.getTargetType(), slider.getItemTarget()));

                        sliderNavList.add(slider);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            if (json.has("theme")) {
                JsonNode themeJson = json.get("theme");
                for (JsonNode themeTemp : themeJson) {
                    try {
                        Theme theme = Json.fromJson(themeTemp, Theme.class);
                        JsonNode imgJson = Json.parse(theme.getThemeImg());
                        theme.setThemeImg(imgJson.get("url").asText());
                        //按照类型处理跳转地址
                        comCtrl.handleThemeUrl(theme);

                        themeList.add(theme);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            return ok(views.html.products.index.render(sliderList,sliderNavList,themeList,pageCount));
        }
        );
    }

    /**
     * Ajax加载首页
     *
     * @return
     * @throws Exception
     */
    public F.Promise<Result> loadIndexAjax(String pageCount){
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            //String pageCount = request().body().asJson().toString();
            Request request = comCtrl.getBuilder(ctx())
                    .url(INDEX_PAGE + pageCount)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("loadIndexAjax接收数据-->\n"+json);
            }
            List<Theme> themeList = new ArrayList<>();
            if (json.has("theme")) {
                JsonNode themeJson = json.get("theme");
                for (JsonNode themeTemp : themeJson) {
                    try {
                        Theme theme = Json.fromJson(themeTemp, Theme.class);
//                        JsonNode imgJson = Json.parse(theme.getThemeImg());
//                        String imgUrl = imgJson.get("url").toString();
//                        imgUrl = imgUrl.substring(1, imgUrl.length() - 1);
//                        theme.setThemeImg(imgUrl);
//                        if (!"h5".equals(theme.getType())) {
//                            String themeUrl = theme.getThemeUrl();
//                            themeUrl = themeUrl.replace(THEME_PAGE, "");
//                            theme.setThemeUrl(themeUrl);
//                        }
                        JsonNode imgJson = Json.parse(theme.getThemeImg());
                        theme.setThemeImg(imgJson.get("url").asText());
                        //按照类型处理跳转地址
                        comCtrl.handleThemeUrl(theme);

                        themeList.add(theme);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                return ok(Json.toJson(themeList));
            }
            return null;
        });
    }

    /**
     * 主题详情页
     *
     * @param url
     * @return
     * @throws Exception
     */
    public F.Promise<Result> themeDetail(String url){
        String hisUrl=comCtrl.pushOrPopHistoryUrl(ctx());
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request = comCtrl.getBuilder(ctx())
                    .url(THEME_PAGE + url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("themeDetail接收数据-->\n"+json);
            }
            String themeImg = "";
            List<Object[]> tagList = new ArrayList<>();
            List<List<ThemeItem>> itemResultList = new ArrayList<>();
            ThemeBasic themeBasic = new ThemeBasic();
            if (json.has("themeList")) {
                themeBasic = Json.fromJson(json.get("themeList"), ThemeBasic.class);
                if(null!=themeBasic.getThemeImg()){
                    JsonNode imgJson = Json.parse(themeBasic.getThemeImg());
                    themeBasic.setThemeImg(imgJson.get("url").asText());
                }


                //主题标签
                if (themeBasic.getMasterItemTag() != null) {
                    JsonNode tagJson = Json.parse(themeBasic.getMasterItemTag());
                    for (JsonNode tag : tagJson) {
                        try {
                            Object[] tagObject = new Object[5];
                            tagObject[0] = tag.get("top").asDouble() * 100;
                            String tagUrl = Json.fromJson(tag.get("url"), String.class);
                            String urlType = "";
                            tagUrl = tagUrl.replace(GOODS_PAGE, "");
                            tagObject[1] = tagUrl;
                            int tagAngle = tag.get("angle").asInt();
                            if (tagAngle == 0) {
                                tagObject[2] = tag.get("left").asDouble() * 100;
                            }
                            if (tagAngle == 180) {
                                tagObject[2] = 100 - tag.get("left").asDouble() * 100;
                            }
                            String tagName = tag.get("name").toString();
                            tagName = tagName.substring(1, tagName.length() - 1);
                            tagObject[3] = tagName;
                            tagObject[4] = tag.get("angle").asInt();
                            tagList.add(tagObject);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                //主题中的商品
                List<ThemeItem> nItemList = new ArrayList<>();
                List<ThemeItem> itemList = themeBasic.getThemeItemList();
                if(null!=itemList&&itemList.size()>0){
                    ThemeItem temp;
                    for (ThemeItem themeItem : itemList) {
                        temp=comCtrl.parseThemeItem(themeItem);
                        if(null!=temp){
                            nItemList.add(themeItem);
                        }
                    }

                    for (int i = 0; i < nItemList.size() / 2; i++) {
                        List<ThemeItem> rowList = new ArrayList<>();
                        rowList.add(itemList.get(i * 2));
                        rowList.add(itemList.get(i * 2 + 1));
                        itemResultList.add(rowList);
                    }
                    if (nItemList.size() % 2 != 0) {
                        List<ThemeItem> rowList = new ArrayList<>();
                        rowList.add(nItemList.get(nItemList.size() - 1));
                        itemResultList.add(rowList);
                    }


                }



            }
            return ok(views.html.products.themeDetail.render(themeBasic, tagList, itemResultList));
        });
    }

    /**
     * 商品明细
     * @param url
     * @return
     * @throws Exception
     */
    public F.Promise<Result> detail(String url){
        String hisUrl=comCtrl.pushOrPopHistoryUrl(ctx());
        comCtrl.addCurUrlCookie(ctx());
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request =comCtrl.getBuilder(ctx()) //该位置需要取个人相关的数据,必须修改
                    // getBuilder(request(), session())
                    .url(GOODS_PAGE + url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("detail接收数据-->\n"+json);
            }
            //当前时间
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strNow = sdfDate.format(now);
            //普通商品
            if (!url.contains("pin")) {
                //商品基本信息
                Item itemMain = new Item();
                //商品参数
                List<Object[]> itemFeaturesList = new ArrayList<>();
                //热卖推荐
                List<List<ThemeItem>> pushResultList = new ArrayList<>();
                //商品Sku
                List<Inventory> inventoryList = new ArrayList<>();
                //Sku商品预览图片
                List<List<String>> preImgList = new ArrayList<>();
                //优惠信息
                List<String> publicityList = new ArrayList<>();
                //商品基本信息
                if (json.has("main")) {
                    JsonNode mainJson = json.get("main");
                    itemMain = Json.fromJson(mainJson, Item.class);
                    //商品参数
                    if (itemMain != null) {
                        JsonNode features = Json.parse(itemMain.getItemFeatures());
                        LinkedHashMap featuresMap = Json.fromJson(features, LinkedHashMap.class); //按照顺序不能用hashmap
                        for (Object key : featuresMap.keySet()) {

                            Object[] featureObj = new Object[2];
                            featureObj[0] = key;
                            featureObj[1] = featuresMap.get(key);
                            itemFeaturesList.add(featureObj);
                        }
                    }
                }
                //商品Sku
                if (json.has("stock")) {
                    JsonNode stockJson = json.get("stock");
                    int disableSkuCount = 0;
                    for (JsonNode stockInv : stockJson) {
                        try {
                            Inventory inventory = Json.fromJson(stockInv, Inventory.class);
                            if ("D".equals(inventory.getState()) || "N".equals(inventory.getState()) || "K".equals(inventory.getState())) {
                                disableSkuCount = disableSkuCount + 1;
                            }
                            JsonNode imgJson = Json.parse(inventory.getInvImg());
                            inventory.setInvImg(imgJson.get("url").asText());
                            JsonNode previewImgJson = Json.parse(inventory.getItemPreviewImgs());
                            List<String> preImgSubList = new ArrayList<>();
                            for (JsonNode previewJson : previewImgJson) {
                                if(null!=previewJson.get("url")){
                                    preImgSubList.add(Json.fromJson(previewJson.get("url"), String.class));
                                }

                            }
                            preImgList.add(preImgSubList);
                            inventoryList.add(inventory);
                        }catch (Exception e){
                            e.printStackTrace();
                            Logger.error("解析详情界面数据异常");
                        }
                    }
                    if(inventoryList.size() == disableSkuCount){
                        itemMain.setState("D");
                    }
                }
                //优惠信息
                if(null!=itemMain.getPublicity()){
                    JsonNode itemPublicity = Json.parse(itemMain.getPublicity());
                    for (JsonNode publicity : itemPublicity) {
                        publicityList.add(Json.fromJson(publicity, String.class));
                    }
                }

                List<ThemeItem> pushList = new ArrayList<>();
                //热卖推荐
                if (json.has("push")) {
                    JsonNode pushJson = json.get("push");
                    for (JsonNode pushTemp : pushJson) {
                        try {
                            ThemeItem themeItem = Json.fromJson(pushTemp, ThemeItem.class);
                            JsonNode imgJson = Json.parse(themeItem.getItemImg());
                            themeItem.setItemImg(imgJson.get("url").asText());
                            themeItem.setItemUrl(themeItem.getItemUrl().replace(GOODS_PAGE, ""));
                            Date endAtDate = sdfDate.parse(themeItem.getEndAt());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(endAtDate);
                            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                            if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
                                hour = "0" + hour;
                            }
                            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                            if (calendar.get(Calendar.MINUTE) < 10) {
                                minute = "0" + minute;
                            }
                            String endDate = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + hour + ":" + minute;

                            if (themeItem.getEndAt().compareTo(strNow) < 0) {
                                themeItem.setEndAt("已结束");
                            } else {
                                themeItem.setEndAt("截止" + endDate);
                            }

                            pushList.add(themeItem);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < pushList.size() / 2; i++) {
                        List<ThemeItem> rowList = new ArrayList<>();
                        rowList.add(pushList.get(i * 2));
                        rowList.add(pushList.get(i * 2 + 1));
                        pushResultList.add(rowList);
                    }
                    if (pushList.size() % 2 != 0) {
                        List<ThemeItem> rowList = new ArrayList<>();
                        rowList.add(pushList.get(pushList.size() - 1));
                        pushResultList.add(rowList);
                    }
                }
                //评论
                CommentNumDTO commentNumDTO=null;
                if(json.has("comment")){
                    commentNumDTO=Json.fromJson(json.get("comment"), CommentNumDTO.class);
                }

                return ok(views.html.products.detail.render(itemMain, itemFeaturesList, pushResultList, inventoryList, inventoryList.size(), preImgList, publicityList,url,hisUrl,commentNumDTO,pushList));
            }
            //拼购商品
            else {
                //商品基本信息
                Item itemMain = new Item();
                //商品参数
                List<Object[]> itemFeaturesList = new ArrayList<>();
                //热卖推荐
                List<List<ThemeItem>> pushResultList = new ArrayList<>();
                //售罄推荐
                List<ThemeItem> pushList = new ArrayList<>();
                //拼购商品
                PinInvDetail pinInvDetail = new PinInvDetail();
                //Sku商品预览图片
                List<String> preImgList = new ArrayList<>();
                //优惠信息
                List<String> publicityList = new ArrayList<>();
                //拼购商品基本信息
                if (json.has("main")) {
                    JsonNode mainJson = json.get("main");
                    itemMain = Json.fromJson(mainJson, Item.class);
                    //优惠信息
                    if(null!=itemMain.getPublicity()){
                        JsonNode itemPublicity = Json.parse(itemMain.getPublicity());
                        for (JsonNode publicity : itemPublicity) {
                            publicityList.add(Json.fromJson(publicity, String.class));
                        }
                    }

                    //商品参数
                    if (itemMain != null&&null!=itemMain.getItemFeatures()) {
                        JsonNode features = Json.parse(itemMain.getItemFeatures());
                        LinkedHashMap featuresMap = Json.fromJson(features, LinkedHashMap.class); //按照顺序不能用hashmap
                        for (Object key : featuresMap.keySet()) {

                            Object[] featureObj = new Object[2];
                            featureObj[0] = key;
                            featureObj[1] = featuresMap.get(key);
                            itemFeaturesList.add(featureObj);
                        }
                    }
                }
                //拼购商品Sku
                if (json.has("stock")) {
                    pinInvDetail = Json.fromJson(json.get("stock"), PinInvDetail.class);
                    JsonNode floorPriceJson = Json.parse(pinInvDetail.getFloorPrice());
                    pinInvDetail.setFloorPrice(floorPriceJson.get("price").asText());
                    pinInvDetail.setFloorPricePersonNum(floorPriceJson.get("person_num").asInt());
                    JsonNode invImgJson = Json.parse(pinInvDetail.getInvImg());
                    pinInvDetail.setInvImg(invImgJson.get("url").asText());
                    String endAt = sdfDate.format(pinInvDetail.getEndAt());
                    if (endAt.compareTo(strNow) < 0 || pinInvDetail.getRestAmount() == 0 || pinInvDetail.getStatus()== "D" || pinInvDetail.getStatus()== "N" || pinInvDetail.getStatus()== "K") {
                        pinInvDetail.setSoldOut(true);
                    }
                    //预览图片
                    JsonNode preImgJson = Json.parse(pinInvDetail.getItemPreviewImgs());
                    for (JsonNode preImg : preImgJson) {
                        preImgList.add(preImg.get("url").asText());
                    }
                }
                //热卖推荐
                if (json.has("push")) {
                    JsonNode pushJson = json.get("push");
                    for (JsonNode pushTemp : pushJson) {
                        try {
                            ThemeItem themeItem = Json.fromJson(pushTemp, ThemeItem.class);
                            JsonNode imgJson = Json.parse(themeItem.getItemImg());
                            themeItem.setItemImg(imgJson.get("url").asText());
                            themeItem.setItemUrl(themeItem.getItemUrl().replace(GOODS_PAGE, ""));
                            Date endAtDate = sdfDate.parse(themeItem.getEndAt());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(endAtDate);
                            String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
                            if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
                                hour = "0" + hour;
                            }
                            String minute = String.valueOf(calendar.get(Calendar.MINUTE));
                            if (calendar.get(Calendar.MINUTE) < 10) {
                                minute = "0" + minute;
                            }
                            String endDate = (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日" + hour + ":" + minute;

                            if (themeItem.getEndAt().compareTo(strNow) < 0 || themeItem.getState() == "D" || themeItem.getState() == "N" || themeItem.getState() == "K") {
                                themeItem.setEndAt("已结束");
                            } else {
                                themeItem.setEndAt("截止" + endDate);
                            }

                            pushList.add(themeItem);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0; i < pushList.size() / 2; i++) {
                        List<ThemeItem> rowList = new ArrayList<>();
                        rowList.add(pushList.get(i * 2));
                        rowList.add(pushList.get(i * 2 + 1));
                        pushResultList.add(rowList);
                    }
                    if (pushList.size() % 2 != 0) {
                        List<ThemeItem> rowList = new ArrayList<>();
                        rowList.add(pushList.get(pushList.size() - 1));
                        pushResultList.add(rowList);
                    }
                }
                //评论
                CommentNumDTO commentNumDTO=null;
                if(json.has("comment")){
                    commentNumDTO=Json.fromJson(json.get("comment"), CommentNumDTO.class);
                }
                return ok(views.html.products.pinDetail.render(itemMain, itemFeaturesList, pinInvDetail, pushResultList, preImgList, pushList, url,hisUrl,commentNumDTO));
            }
        });
    }

    /**
     * 拼购玩法说明
     *
     * @return
     */
    public Result pinInstruction() {
        String hisUrl=comCtrl.pushOrPopHistoryUrl(ctx());
        return ok(views.html.products.pinInstruction.render(hisUrl));
    }

    /**
     * 拼购商品的价格阶梯
     *
     * @param url
     * @return
     * @throws Exception
     */
    public F.Promise<Result> pinTieredPrice(String url){
        String hisUrl=comCtrl.getHistoryUrl(ctx());
        comCtrl.addCurUrlCookie(ctx());
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request = getBuilder(request(), session())
                    .url(GOODS_PAGE + url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("pinTieredPrice接收数据-->\n"+json);
            }
            if (json.has("stock")) {
                JsonNode stockJson = json.get("stock");
                JsonNode floorPriceJson = Json.parse(Json.fromJson(stockJson.get("floorPrice"), String.class));
                PinInvDetail pinInvDetail = Json.fromJson(json.get("stock"), PinInvDetail.class);
                pinInvDetail.setInvImg(comCtrl.getImgUrl(pinInvDetail.getInvImg()));
                pinInvDetail.setFloorPricePersonNum(floorPriceJson.get("person_num").asInt());
                pinInvDetail.setFloorPricePrice(Json.fromJson(floorPriceJson.get("price"), BigDecimal.class));
                Collections.sort(pinInvDetail.getPinTieredPrices(), new Comparator<PinTieredPrice>() {
                    @Override
                    public int compare(PinTieredPrice tieredPrice2, PinTieredPrice tieredPrice1) {
                        return tieredPrice1.getPeopleNum().compareTo(tieredPrice2.getPeopleNum());
                    }
                });
                return ok(views.html.products.pinTieredPrice.render(pinInvDetail,hisUrl,url));
            }
            return badRequest(views.html.error500.render());
        });
    }


    /**滑动列表页**/
    public F.Promise<Result> getNav(long navId,int pageNum,int op) {
        if(op==1){
            comCtrl.pushOrPopHistoryUrl(ctx());
        }
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request =comCtrl.getBuilder(ctx())
                    .url(NAV_PAGE +navId+"/"+pageNum)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("getNav接收数据-->\n"+json);
            }
            List<List<ThemeItem>> itemResultList = new ArrayList<>();
            List<ThemeItem> itemList=null;
            List<ThemeItem> nItemList=new ArrayList<ThemeItem>();
            if(json.has("themeItemList")) {
                itemList = new ObjectMapper().readValue(json.get("themeItemList").toString(), new TypeReference<List<ThemeItem>>() {
                });
            }
            if(null!=itemList&&itemList.size()>0){
                ThemeItem temp;
                for (ThemeItem themeItem : itemList) {
                    temp=comCtrl.parseThemeItem(themeItem);
                    if(null!=temp){
                        nItemList.add(themeItem);
                    }
                }
                for (int i = 0; i < nItemList.size() / 2; i++) {
                    List<ThemeItem> rowList = new ArrayList<>();
                    rowList.add(itemList.get(i * 2));
                    rowList.add(itemList.get(i * 2 + 1));
                    itemResultList.add(rowList);
                }
                if (nItemList.size() % 2 != 0) {
                    List<ThemeItem> rowList = new ArrayList<>();
                    rowList.add(nItemList.get(nItemList.size() - 1));
                    itemResultList.add(rowList);
                }
            }
            if(op==2){
                return ok(Json.toJson(itemResultList));
            }
            int pageCount=0;
            if(json.has("page_count")){
                pageCount=json.get("page_count").asInt();
            }
            String title="";
            if(json.has("title")){
                title=json.get("title").asText();
            }
            return ok(views.html.products.navlist.render(itemResultList,pageCount,title,navId));
        });

    }

    /**
     * 主题分类
     * @param themeCateCode
     * @param pageNum
     * @param op
     * @return
     */
    public F.Promise<Result> getThemeByCate(int themeCateCode,int pageNum,int op) {
        if(op==1){
            comCtrl.pushOrPopHistoryUrl(ctx());
        }
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request =comCtrl.getBuilder(ctx())
                    .url(THEMECATE_PAGE +themeCateCode+"/"+pageNum)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("getThemeByCate接收数据-->\n"+json);
            }

            List<Theme> themeList = new ArrayList<>();
            if (json.has("theme")) {
                JsonNode themeJson = json.get("theme");
                for (JsonNode themeTemp : themeJson) {
                    try {
                        Theme theme = Json.fromJson(themeTemp, Theme.class);
                        JsonNode imgJson = Json.parse(theme.getThemeImg());
                        theme.setThemeImg(imgJson.get("url").asText());
                        //按照类型处理跳转地址
                        comCtrl.handleThemeUrl(theme);
                        themeList.add(theme);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            if(op==2){
                return ok(Json.toJson(themeList));
            }
            int pageCount =0;
            if(json.has("page_count")){
                pageCount=json.get("page_count").asInt();
            }
            return ok(views.html.products.themecate.render(themeList,pageCount,themeCateCode));
        });

    }

    /**
     * 获取推荐商品
     * @param position
     * @return
     */
    public F.Promise<Result> getRecommendSku(int position){
        F.Promise<JsonNode> promise = F.Promise.promise(() -> {
            Request request =comCtrl.getBuilder(ctx())
                    .url(RECOMMEND_PAGE +position)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String result = dealToString(response);
                if (result != null) {
                    return Json.parse(result);
                } else throw new IOException("Unexpected code" + response);
            } else throw new IOException("Unexpected code" + response);
        });
        return promise.map((F.Function<JsonNode, Result>) json -> {
            if(LOG_OPEN){
                Logger.info("getRecommendSku接收数据-->\n"+json);
            }

            List<List<ThemeItem>> itemResultList = new ArrayList<>();
            List<ThemeItem> itemList=null;
            List<ThemeItem> nItemList=new ArrayList<ThemeItem>();
            if(json.has("themeItemList")) {
                itemList = new ObjectMapper().readValue(json.get("themeItemList").toString(), new TypeReference<List<ThemeItem>>() {
                });
            }
            if(null!=itemList&&itemList.size()>0){
                ThemeItem temp;
                for (ThemeItem themeItem : itemList) {
                    temp=comCtrl.parseThemeItem(themeItem);
                    if(null!=temp){
                        nItemList.add(themeItem);
                    }
                }
                for (int i = 0; i < nItemList.size() / 2; i++) {
                    List<ThemeItem> rowList = new ArrayList<>();
                    rowList.add(itemList.get(i * 2));
                    rowList.add(itemList.get(i * 2 + 1));
                    itemResultList.add(rowList);
                }
                if (nItemList.size() % 2 != 0) {
                    List<ThemeItem> rowList = new ArrayList<>();
                    rowList.add(nItemList.get(nItemList.size() - 1));
                    itemResultList.add(rowList);
                }
            }

            return ok(Json.toJson(itemResultList));
        });
    }


}
