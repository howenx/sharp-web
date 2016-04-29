package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sibyl.sun on 16/4/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRemarkDTO implements Serializable {
    private CartSkuDto orderLine;
    private Remark comment;

    public CartSkuDto getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(CartSkuDto orderLine) {
        this.orderLine = orderLine;
    }

    public Remark getComment() {
        return comment;
    }

    public void setComment(Remark comment) {
        this.comment = comment;
    }
}
