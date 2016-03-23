package domain;

import java.io.Serializable;

/**
 * 添加购物车上传数据
 * Created by sibyl.sun on 16/3/23.
 */
public class CartAddInfo implements Serializable {
    private Long cartId;//购物车ID
    private Long skuId;//skuID
    private Integer amount;//购买数量
    private String state;//状态
    private String skuType;//商品类型 1.vary,2.item,3.customize,4.pin
    private Long skuTypeId;//商品类型所对应的ID

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
}
