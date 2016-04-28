package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * 携带一个商品数据
 * Created by sibyl.sun on 16/4/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SkuInfo implements Serializable {
    private String skuTitle;
    private String skuType;
    private Long skuTypeId;
    private String invImg;
    private Long orderId;

    public String getSkuTitle() {
        return skuTitle;
    }

    public void setSkuTitle(String skuTitle) {
        this.skuTitle = skuTitle;
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

    public String getInvImg() {
        return invImg;
    }

    public void setInvImg(String invImg) {
        this.invImg = invImg;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
