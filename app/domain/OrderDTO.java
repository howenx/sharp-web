package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * 我的订单数据
 * Created by sibyl.sun on 16/3/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO implements Serializable {
    private Address address;
    private Order order;
    private List<CartSkuDto> sku;
    private Refund refund;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<CartSkuDto> getSku() {
        return sku;
    }

    public void setSku(List<CartSkuDto> sku) {
        this.sku = sku;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }
}
