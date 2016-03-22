package domain;

import java.io.Serializable;

/**
 * 提交的结算商品
 * Created by sibyl.sun on 16/3/21.
 */
public class CartInfo implements Serializable {
    private Long cartId;//购物车ID
    private Long skuId;//skuID
    private Integer amount;//购买数量
    private String state;//状态
    private String skuType;//商品类型 1.vary,2.item,3.customize,4.pin
    private Long skuTypeId;//商品类型所对应的ID
    private Long pinTieredPriceId;//拼购价格ID

    private String skuTitle;//标题
    private String skuInvImg;//图片
    private String skuPrice;//价格

    public CartInfo(Long cartId, Long skuId, Integer amount, String state, String skuType, Long skuTypeId, Long pinTieredPriceId,String skuTitle,String skuInvImg,String skuPrice) {
        this.cartId = cartId;
        this.skuId = skuId;
        this.amount = amount;
        this.state = state;
        this.skuType = skuType;
        this.skuTypeId = skuTypeId;
        this.pinTieredPriceId = pinTieredPriceId;
        this.skuTitle=skuTitle;
        this.skuInvImg=skuInvImg;
        this.skuPrice=skuPrice;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSkuType() {
        return skuType;
    }

    public void setSkuType(String skuType) {
        this.skuType = skuType;
    }

    public Long getSkuTypeId() {
        return skuTypeId;
    }

    public void setSkuTypeId(Long skuTypeId) {
        this.skuTypeId = skuTypeId;
    }

    public Long getPinTieredPriceId() {
        return pinTieredPriceId;
    }

    public void setPinTieredPriceId(Long pinTieredPriceId) {
        this.pinTieredPriceId = pinTieredPriceId;
    }

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle;
    }

    public String getSkuInvImg() {
        return skuInvImg;
    }

    public void setSkuInvImg(String skuInvImg) {
        this.skuInvImg = skuInvImg;
    }

    public String getSkuPrice() {
        return skuPrice;
    }

    public void setSkuPrice(String skuPrice) {
        this.skuPrice = skuPrice;
    }
}
