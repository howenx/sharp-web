package mapper;

import domain.Cart;

import java.util.List;

/**
 * Theme mapper interface.
 * Created by howen on 15/10/26.
 */
public interface ShoppingCartMapper {
    List<Cart> getCartByUserSku(Cart cart) throws Exception;
}
