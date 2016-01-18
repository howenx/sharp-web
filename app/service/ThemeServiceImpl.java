package service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.Application;
import domain.*;
import mapper.ThemeMapper;
import play.Logger;
import play.libs.Json;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * implements Service and transaction processing.
 * Created by howen on 15/10/26.
 */
public class ThemeServiceImpl implements ThemeService {

    @Inject
    private ThemeMapper themeMapper;

    //将Json串转换成List
    final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public Optional<List<Theme>> getThemes(int pageSize, int offset) {

        Map<String, Integer> params = new HashMap<>();
        params.put("pageSize", pageSize);
        params.put("offset", offset);
        return Optional.ofNullable(themeMapper.getThemes(params));
    }

    @Override
    public Optional<JsonNode> getThemeList(Long themeId) {
        //先获取主题里的所有itemId
        Theme theme = new Theme();
        theme.setId(themeId);
        Optional<Theme> themeOptional = Optional.ofNullable(themeMapper.getThemeBy(theme));
        if (themeOptional.isPresent()) {
            theme = themeOptional.get();
            ThemeBasic themeBasic = new ThemeBasic();
            themeBasic.setThemeId(themeId);

            JsonNode jsonNodeTag = Json.parse(theme.getMasterItemTag());
            if (jsonNodeTag.isArray()) {
                for (final JsonNode url : jsonNodeTag) {
                    if (url.has("url")) {
                        ((ObjectNode) url).put("url", Application.DEPLOY_URL + url.findValue("url").asText());
                    }
                }
            }
            themeBasic.setMasterItemTag(Json.stringify(jsonNodeTag));

            Pattern p = Pattern.compile("\\d+");
            JsonNode jsonNodeTagAndroid = Json.parse(theme.getMasterItemTag());
            if (jsonNodeTagAndroid.isArray()) {
                for (final JsonNode url : jsonNodeTagAndroid) {
                    if (url.has("url")) {
                        Matcher m = p.matcher(url.findValue("url").asText());
                        while (m.find()) {
                            ((ObjectNode) url).put("url", Application.DEPLOY_URL + "/comm/detail/web/" + m.group());
                        }

                    }
                }
            }
            themeBasic.setMasterItemTagAndroid(Json.stringify(jsonNodeTagAndroid));

            JsonNode jsonNode_ThemeMasterImg = Json.parse(theme.getThemeMasterImg());
            if (jsonNode_ThemeMasterImg.has("url")) {
                ((ObjectNode) jsonNode_ThemeMasterImg).put("url", Application.IMAGE_URL + jsonNode_ThemeMasterImg.get("url").asText());
                themeBasic.setThemeImg(Json.stringify(jsonNode_ThemeMasterImg));//主题主商品宣传图
            }

            List<ThemeItem>  themeItems = new ArrayList<>();

            JsonNode jsonNode = Json.parse(theme.getThemeItem());

            for (final JsonNode objNode : jsonNode) {
                //获取每一个Item
                Item item = new Item();
                item.setId(objNode.asLong());
                item = themeMapper.getItemBy(item);
                //找到每一个主库存信息
                Inventory inventory = new Inventory();
                inventory.setItemId(item.getId());
                inventory.setOrMasterInv(true);
                List<Inventory> inventoryList = themeMapper.getInvBy(inventory);
                if (inventoryList.size() != 0) {
                    //去找到主sku
                    inventory = inventoryList.get(0);

                    ThemeItem themeItem = new ThemeItem();

                    themeItem.setItemId(inventory.getItemId());
                    themeItem.setItemTitle(inventory.getInvTitle());
                    themeItem.setCollectCount(item.getCollectCount());
                    themeItem.setItemDiscount(inventory.getItemDiscount());
                    JsonNode jsonNodeInvImg = Json.parse(inventory.getInvImg());
                    if (jsonNodeInvImg.has("url")) {
                        ((ObjectNode) jsonNodeInvImg).put("url", Application.IMAGE_URL + jsonNodeInvImg.get("url").asText());
                        themeItem.setItemImg(Json.stringify(jsonNodeInvImg));
                    }
                    themeItem.setItemPrice(inventory.getItemPrice());
                    themeItem.setItemSoldAmount(inventory.getSoldAmount());
                    themeItem.setItemSrcPrice(inventory.getItemSrcPrice());
                    themeItem.setItemTitle(inventory.getInvTitle());
                    themeItem.setItemUrl( Application.DEPLOY_URL + "/comm/detail/" + item.getId());
                    themeItem.setItemUrlAndroid(Application.DEPLOY_URL + "/comm/detail/web/" + item.getId());


                    //遍历所有sku状态,如果所有则sku状态均为下架或者售空,提示商品是售空或者下架
                    Inventory invState = new Inventory();
                    inventory.setItemId(item.getId());
                    List<Inventory> invStateList = themeMapper.getInvBy(inventory);
                    int countK = 0;
                    int countD = 0;
                    int count = 0;
                    //单个sku状态  'Y'--正常,'D'--下架,'N'--删除,'K'--售空
                    for (Inventory inv : invStateList) {
                        switch (inv.getState()) {
                            case "Y":
                                themeItem.setState("Y");
                                break;
                            case "K":
                                countK++;
                                break;
                            case "D":
                                countD++;
                                break;
                            default:
                                count++;
                                break;
                        }
                    }
                    if (count == invStateList.size()) {
                        themeItem.setState("K");
                    }
                    if (countK == invStateList.size()) {
                        themeItem.setState("K");//商品状态
                    }
                    if (countD == invStateList.size()) {
                        themeItem.setState("D");//商品状态
                    }

                    themeItems.add(themeItem);
                }
            }

            themeBasic.setThemeItemList(themeItems);

            return Optional.ofNullable(Json.toJson(themeBasic));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Slider>> getSlider() {
        return Optional.ofNullable(themeMapper.getSlider());
    }

    /**
     * 组装返回详细页面数据
     *
     * @param id 商品主键
     * @return map
     */
    @Override
    public Optional<Map<String, Object>> getItemDetail(Long id, Long skuId) {

        Item item = new Item();
        item.setId(id);

        Map<String, Object> map = new HashMap<>();
        Optional<Item> itemOptional = Optional.ofNullable(themeMapper.getItemBy(item));
        if (itemOptional.isPresent()) {
            try {
                item = itemOptional.get();
                //将Json字符串转成list
                List<List<String>> listList = mapper.readValue(item.getItemDetailImgs(), mapper.getTypeFactory().constructCollectionType(List.class, List.class));

                List<String> detailsList = new ArrayList<>();
                listList.forEach(l -> {
                    l.forEach(detailsList::add);
                });

                //使用Java8 Stream写法,增加图片地址前缀
                item.setItemDetailImgs(Json.toJson(detailsList.stream().map((s) -> {
                    return Application.IMAGE_URL + s;
                }).collect(Collectors.toList())).toString());

//                item.setItemMasterImg(Application.IMAGE_URL + item.getItemMasterImg());

                map.put("main", item);

                Inventory inventory = new Inventory();
                inventory.setItemId(item.getId());

                //遍历库存list 对其进行相应的处理
                List<Inventory> list = themeMapper.getInvBy(inventory).stream().map(l -> {

                    //拼接sku链接
                    if (null != l.getInvUrl() && !"".equals(l.getInvUrl())) {
                        l.setInvUrl(Application.DEPLOY_URL + l.getInvUrl());
                    } else {
                        l.setInvUrl(Application.DEPLOY_URL + "/comm/detail/" + id + "/" + l.getId());
                    }

                    //SKU图片
                    if (l.getInvImg().contains("url")) {
                        JsonNode jsonNode_InvImg = Json.parse(l.getInvImg());
                        if (jsonNode_InvImg.has("url")) {
                            ((ObjectNode) jsonNode_InvImg).put("url", Application.IMAGE_URL + jsonNode_InvImg.get("url").asText());
                            l.setInvImg(Json.stringify(jsonNode_InvImg));
                        }
                    } else l.setInvImg(Application.IMAGE_URL + l.getInvImg());


                    //判断是否是当前需要显示的sku
                    if (!skuId.equals(((Integer) (-1)).longValue()) && !l.getId().equals(skuId)) {
                        l.setOrMasterInv(false);
                    } else if (l.getId().equals(skuId)) {
                        l.setOrMasterInv(true);
                    }

                    JsonNode js = Json.parse(l.getItemPreviewImgs());

                    if (js.isArray()) {
                        for (JsonNode j : js) {
                            if (j.has("url")) {
                                ((ObjectNode) j).put("url", Application.IMAGE_URL + j.get("url").asText());
                            }
                        }
                    }
                    l.setItemPreviewImgs(js.toString());

                    return l;
                }).collect(Collectors.toList());

                map.put("stock", list);
                return Optional.of(map);
            } catch (Exception ex) {
                Logger.error("getItemDetail: " + ex.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * 组装返回详细页面数据
     *
     * @param id 商品主键
     * @return map
     */
    @Override
    public Optional<Map<String, Object>> getItemDetailWeb(Long id, Long skuId) {

        Item item = new Item();
        item.setId(id);

        Map<String, Object> map = new HashMap<>();
        Optional<Item> itemOptional = Optional.ofNullable(themeMapper.getItemBy(item));
        if (itemOptional.isPresent()) {
            try {
                item = itemOptional.get();

                List<List<String>> listList = mapper.readValue(item.getItemDetailImgs(), mapper.getTypeFactory().constructCollectionType(List.class, List.class));

                List<String> detailsList = new ArrayList<>();
                listList.forEach(l -> {
                    l.forEach(detailsList::add);
                });

                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder("<!DOCTYPE HTML><html><meta charset='UTF-8'><title>Image Canvas</title></head><style>body {margin: 0px;} p {width: 100%;line-height: 24px;font-size: 12px;text-align: left;margin: 0px auto 0 auto;padding: 0;}p img{float: none;margin: 0;padding: 0;border: 0;vertical-align: top;}</style><body><p>");

                //使用Java8 Stream写法,增加图片地址前缀
                detailsList.stream().map((s) -> {
                    stringBuilder.append("<img width=\"100%\" src=\"").append(Application.IMAGE_URL).append(s).append("\">");
                    return Application.IMAGE_URL + s;
                }).collect(Collectors.toList());

                stringBuilder.append("</p></body></html>");

                item.setItemDetailImgs(stringBuilder.toString());

//                item.setItemMasterImg(Application.IMAGE_URL + item.getItemMasterImg());

                map.put("main", item);

                Inventory inventory = new Inventory();
                inventory.setItemId(item.getId());

                //遍历库存list 对其进行相应的处理
                List<Inventory> list = themeMapper.getInvBy(inventory).stream().map(l -> {

                    //拼接sku链接
                    if (null != l.getInvUrl() && !"".equals(l.getInvUrl())) {
                        l.setInvUrl(controllers.Application.DEPLOY_URL + l.getInvUrl());
                    } else {
                        l.setInvUrl(controllers.Application.DEPLOY_URL + "/comm/detail/" + id + "/" + l.getId());
                    }

                    //SKU图片
                    if (l.getInvImg().contains("url")) {
                        JsonNode jsonNode_InvImg = Json.parse(l.getInvImg());
                        if (jsonNode_InvImg.has("url")) {
                            ((ObjectNode) jsonNode_InvImg).put("url", Application.IMAGE_URL + jsonNode_InvImg.get("url").asText());
                            l.setInvImg(Json.stringify(jsonNode_InvImg));
                        }
                    } else l.setInvImg(Application.IMAGE_URL + l.getInvImg());

                    //判断是否是当前需要显示的sku
                    if (!skuId.equals(((Integer) (-1)).longValue()) && !l.getId().equals(skuId)) {
                        l.setOrMasterInv(false);
                    } else if (l.getId().equals(skuId)) {
                        l.setOrMasterInv(true);
                    }

                    JsonNode js = Json.parse(l.getItemPreviewImgs());

                    if (js.isArray()) {
                        for (JsonNode j : js) {
                            if (j.has("url")) {
                                ((ObjectNode) j).put("url", Application.IMAGE_URL + j.get("url").asText());
                            }
                        }
                    }
                    l.setItemPreviewImgs(js.toString());

                    return l;
                }).collect(Collectors.toList());

                map.put("stock", list);

                return Optional.of(map);
            } catch (Exception ex) {
                Logger.error("getItemDetail: " + ex.getMessage());
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public void setThemeMapper(ThemeMapper themeMapper) {
        this.themeMapper = themeMapper;
    }
}
