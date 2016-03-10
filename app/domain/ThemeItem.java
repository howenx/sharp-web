package domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import util.MoneySerializer;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Table ThemeItem model.
 * Created by howen on 15/10/26.
 */
public class ThemeItem implements Serializable {

    private     String          itemImg;   	            //商品图片
    private     String          itemUrl;   		        //商品详细页面链接

    private     String          itemTitle;   	        //商品标题
    @JsonSerialize(using = MoneySerializer.class)
    private     BigDecimal      itemSrcPrice;   	    //商品价格
    @JsonSerialize(using = MoneySerializer.class)
    private     BigDecimal      itemPrice;              //商品原价
    @JsonSerialize(using = MoneySerializer.class)
    private     BigDecimal      itemDiscount;           //商品折扣
    private     Integer         itemSoldAmount;         //商品销量
    private     Integer         collectCount;           //收藏数
    private     String          state;                  //商品状态


    private     String          itemType;               //主题展示的商品类型
    private     String          startAt;                //开售时间
    private     String          endAt;                  //截止时间


    @JsonIgnore
    private     Long            itemId;   	            //商品ID
    @JsonIgnore
    private     String          invArea;                //库存区域
    @JsonIgnore
    private     String          postalTaxRate;          //税率,百分比
    @JsonIgnore
    private     String          postalStandard;         //关税收费标准
    @JsonIgnore
    private     String          invAreaNm;              //仓储地名称
    @JsonIgnore
    private     String          invWeight;              //商品重量单位g
    @JsonIgnore
    private     String          itemUrlAndroid;   		//商品详细页面链接Android

    public ThemeItem() {
    }


    public ThemeItem(Long itemId, String itemImg, String itemUrl, String itemUrlAndroid, String itemTitle, BigDecimal itemSrcPrice, BigDecimal itemPrice, BigDecimal itemDiscount, Integer itemSoldAmount, Integer collectCount, String state, String invArea, String postalTaxRate, String postalStandard, String invAreaNm, String invWeight, String itemType, String startAt, String endAt) {
        this.itemId = itemId;
        this.itemImg = itemImg;
        this.itemUrl = itemUrl;
        this.itemUrlAndroid = itemUrlAndroid;
        this.itemTitle = itemTitle;
        this.itemSrcPrice = itemSrcPrice;
        this.itemPrice = itemPrice;
        this.itemDiscount = itemDiscount;
        this.itemSoldAmount = itemSoldAmount;
        this.collectCount = collectCount;
        this.state = state;
        this.invArea = invArea;
        this.postalTaxRate = postalTaxRate;
        this.postalStandard = postalStandard;
        this.invAreaNm = invAreaNm;
        this.invWeight = invWeight;
        this.itemType = itemType;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public String getItemUrlAndroid() {
        return itemUrlAndroid;
    }

    public void setItemUrlAndroid(String itemUrlAndroid) {
        this.itemUrlAndroid = itemUrlAndroid;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public BigDecimal getItemSrcPrice() {
        return itemSrcPrice;
    }

    public void setItemSrcPrice(BigDecimal itemSrcPrice) {
        this.itemSrcPrice = itemSrcPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public BigDecimal getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(BigDecimal itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public Integer getItemSoldAmount() {
        return itemSoldAmount;
    }

    public void setItemSoldAmount(Integer itemSoldAmount) {
        this.itemSoldAmount = itemSoldAmount;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getInvArea() {
        return invArea;
    }

    public void setInvArea(String invArea) {
        this.invArea = invArea;
    }

    public String getPostalTaxRate() {
        return postalTaxRate;
    }

    public void setPostalTaxRate(String postalTaxRate) {
        this.postalTaxRate = postalTaxRate;
    }

    public String getPostalStandard() {
        return postalStandard;
    }

    public void setPostalStandard(String postalStandard) {
        this.postalStandard = postalStandard;
    }

    public String getInvAreaNm() {
        return invAreaNm;
    }

    public void setInvAreaNm(String invAreaNm) {
        this.invAreaNm = invAreaNm;
    }

    public String getInvWeight() {
        return invWeight;
    }

    public void setInvWeight(String invWeight) {
        this.invWeight = invWeight;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    @Override
    public String toString() {
        return "ThemeItem{" +
                "itemId=" + itemId +
                ", itemImg='" + itemImg + '\'' +
                ", itemUrl='" + itemUrl + '\'' +
                ", itemUrlAndroid='" + itemUrlAndroid + '\'' +
                ", itemTitle='" + itemTitle + '\'' +
                ", itemSrcPrice=" + itemSrcPrice +
                ", itemPrice=" + itemPrice +
                ", itemDiscount=" + itemDiscount +
                ", itemSoldAmount=" + itemSoldAmount +
                ", collectCount=" + collectCount +
                ", state='" + state + '\'' +
                ", invArea='" + invArea + '\'' +
                ", postalTaxRate='" + postalTaxRate + '\'' +
                ", postalStandard='" + postalStandard + '\'' +
                ", invAreaNm='" + invAreaNm + '\'' +
                ", invWeight='" + invWeight + '\'' +
                ", itemType='" + itemType + '\'' +
                ", startAt='" + startAt + '\'' +
                ", endAt='" + endAt + '\'' +
                '}';
    }
}
