package service;

import domain.Cart;
import mapper.ShoppingCartMapper;

import javax.inject.Inject;
import java.util.List;

/**
 * 购物车service实现
 * Created by howen on 15/11/22.
 */
public class CartServiceImpl implements CartService {

    @Inject
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public List<Cart> getCartByUserSku(Cart cart) throws Exception {
        return shoppingCartMapper.getCartByUserSku(cart);
    }

}
