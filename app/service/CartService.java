package service;

import domain.Cart;

import java.util.List;

/**
 * Cart service
 * Created by howen on 15/11/22.
 */
public interface CartService {

    List<Cart> getCartByUserSku(Cart cart) throws Exception;
}
