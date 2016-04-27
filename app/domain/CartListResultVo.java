package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * 用于购物车返回数据解析
 * Created by howen on 16/3/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CartListResultVo implements Serializable{

    private Message message;
    private List<CartItemDTO> cartList;

    public CartListResultVo() {
    }

    public CartListResultVo(Message message, List<CartItemDTO> cartList) {
        this.message = message;
        this.cartList = cartList;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public List<CartItemDTO> getCartList() {
        return cartList;
    }

    public void setCartList(List<CartItemDTO> cartList) {
        this.cartList = cartList;
    }

    @Override
    public String toString() {
        return "CartListResultVo{" +
                "message=" + message +
                ", cartList=" + cartList +
                '}';
    }
}
