package modules;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * 查询参数表中的参数项
 * Created by hao on 16/2/28.
 */
@Singleton
public class SysParCom {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    //图片服务器url
    public static String IMAGE_URL;


    public static String INDEX_PAGE;
    public static final OkHttpClient client = new OkHttpClient();
    public static String LOGIN_PAGE;//用户登录
    public static String COLLECT_PAGE;
    public static String COLLECT_DEL; //取消收藏
    public static String COLLECT_SUBMIT;//收藏
    public static String ADDRESS_PAGE;//地址
    public static String ADDRESS_ADD;//地址新增
    public static String ADDRESS_DEL;//地址删除
    public static String ADDRESS_UPDATE;//地址更新
    public static String COUPON_PAGE;//我的优惠券
    public static String FEEDBACK_PAGE;//意见反馈
    public static String ORDER_PAGE;//我的订单
    public static String ORDER_CANCEL;//取消订单
    public static String ORDER_DEL;//删除订单
    public static String ORDER_REFUND;//申请售后
    public static String ORDER_VERIFY;//订单校验

    public static String PIN_LIST;//我的拼团
    public static String PIN_ACTIVITY;//拼团详情
    public static String PIN_ORDER_DETAIL;//拼团详情




    public static String PHONE_VERIFY;//手机号检测
    public static String PHONE_CODE;//发送短信验证码
    public static String REGISTER_PAGE;//用户注册
    public static String RESET_PASSWORD;//密码修改
    public static String USER_INFO;//用户信息获取
    public static String USER_UPDATE;//用户信息修改
    public static String VIEWS_AGREEMENT;//服务条款
    public static String VIEWS_ABOUT;//关于我们
    public static String VIEWS_PRIVACY;//隐私权政策


    public static String THEME_PAGE;//主题
    public static String GOODS_PAGE;//商品详情
//    public static String PIN_PAGE;//拼购商品
//    public static String ITEM_PAGE;//普通商品,多样化价格商品
//    public static String VARY_PAGE;//多样化价格商品
//    public static String CUSTOMIZE_PAGE;//主题中自定义价格商品


    public static String IMAGE_CODE;//图形验证码







    /******** 购物相关 ********/
    public static  String SHOPPING_LIST;//购物车列表
    public static  String SHOPPING_ADDTOCART;//加入购物车
    public static  String SHOPPING_SETTLE;//结算
    public static  String ORDER_SUBMIT;//提交订单
    public static  String CART_ADD;//加入购物车
    public static  String CART_DEL;//删除购物车
    public static  String CART_AMOUNT;//购物车数量

    public static  String PAY_ORDER;

    public static  String PAY_URL;//去支付





    @Inject
    public SysParCom(Configuration configuration) {

        IMAGE_URL = configuration.getString("image.server.url");


        INDEX_PAGE = configuration.getString("products.page.index");
        LOGIN_PAGE = configuration.getString("user.page.login");
        COLLECT_PAGE=configuration.getString("shopping.collect.list");
        COLLECT_DEL=configuration.getString("shopping.collect.del");
        COLLECT_SUBMIT=configuration.getString("shopping.collect.submit");
        //地址
        ADDRESS_PAGE=configuration.getString("user.address.list");
        ADDRESS_ADD=configuration.getString("user.address.add");
        ADDRESS_DEL=configuration.getString("user.address.del");
        ADDRESS_UPDATE=configuration.getString("user.address.update");


        COUPON_PAGE=configuration.getString("shopping.coupons");
        FEEDBACK_PAGE=configuration.getString("shopping.feedback");
        //我的订单
        ORDER_PAGE=configuration.getString("shopping.order.all");
        ORDER_CANCEL=configuration.getString("shopping.order.cancel");
        ORDER_DEL=configuration.getString("shopping.order.del");
        ORDER_REFUND=configuration.getString("shopping.order.refund");
        ORDER_VERIFY=configuration.getString("shopping.order.verify");

        //我的拼团
        PIN_LIST=configuration.getString("promotion.pin.list");
        PIN_ACTIVITY=configuration.getString("promotion.pin.activity");
        PIN_ORDER_DETAIL=configuration.getString("shopping.pin.detail");


        //注册与密码
        PHONE_VERIFY = configuration.getString("user.phone.verify");
        PHONE_CODE = configuration.getString("user.phone.code");
        REGISTER_PAGE = configuration.getString("user.page.register");
        RESET_PASSWORD = configuration.getString("user.reset.password ");
        VIEWS_AGREEMENT = configuration.getString("services.views.agreement");
        VIEWS_ABOUT = configuration.getString("services.views.about");
        VIEWS_PRIVACY = configuration.getString("services.views.privacy");

        //用户信息
        USER_INFO = configuration.getString("user.get.info");
        USER_UPDATE = configuration.getString("user.page.update");

        //商品信息
        THEME_PAGE = configuration.getString("products.page.theme");
        GOODS_PAGE = configuration.getString("products.page.goods");
//        PIN_PAGE = configuration.getString("products.page.pin");
//        ITEM_PAGE = configuration.getString("products.page.item");
//        VARY_PAGE = configuration.getString("products.page.vary");
//        CUSTOMIZE_PAGE = configuration.getString("products.page.customize");

        IMAGE_CODE =configuration.getString("user.page.imagecode");




        /******** 购物相关 ********/
        SHOPPING_LIST = configuration.getString("shopping.page.cart.list");
        SHOPPING_ADDTOCART = configuration.getString("shopping.page.cart.add");
        SHOPPING_SETTLE=configuration.getString("shopping.settle");
        ORDER_SUBMIT=configuration.getString("shopping.order.submit");
        CART_ADD=configuration.getString("shopping.cart.add");
        CART_DEL=configuration.getString("shopping.cart.del");
        CART_AMOUNT=configuration.getString("services.cart.amount");


        PAY_ORDER=configuration.getString("shopping.pay.order");
        PAY_URL=configuration.getString("shopping.pay.url");







    }
}
