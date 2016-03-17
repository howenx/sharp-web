package modules;

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
    //图片服务器url
    public static String IMAGE_URL;


    public static String INDEX_PAGE;
    public static final OkHttpClient client = new OkHttpClient();
    public static String LOGIN_PAGE;//用户登录
    public static String COLLECT_PAGE;
    public static String COLLECT_DEL; //取消收藏
    public static String ADDRESS_PAGE;//地址
    public static String ADDRESS_ADD;//地址新增
    public static String ADDRESS_DEL;//地址删除
    public static String ADDRESS_UPDATE;//地址更新
    public static String COUPON_PAGE;//我的优惠券
    public static String ORDER_PAGE;//我的订单
    public static String ORDER_CANCEL;//取消订单
    public static String ORDER_DEL;//删除订单

    public static String PIN_LIST;//我的拼团
    public static String PIN_ACTIVITY;//拼团详情
    public static String PIN_ORDER_DETAIL;//拼团详情



    public static String PHONE_VERIFY;//手机号检测
    public static String PHONE_CODE;//发送短信验证码
    public static String REGISTER_PAGE;//用户注册
    public static String RESET_PASSWORD;//密码修改
    public static String USER_INFO;//用户信息获取
    public static String USER_UPDATE;//用户信息修改

    public static String ITEM_PAGE;//普通商品,多样化价格商品
    public static String THEME_PAGE;//主题
    public static String PIN_PAGE;//拼购商品
    public static String SUBJECT_PAGE;//主题中自定义价格商品


    public static String IMAGE_CODE;//图形验证码







    /******** 购物相关 ********/
    public static  String SHOPPING_LIST;//购物车列表











    @Inject
    public SysParCom(Configuration configuration) {

        IMAGE_URL = configuration.getString("image.server.url");


        INDEX_PAGE = configuration.getString("products.page.index");
        LOGIN_PAGE = configuration.getString("user.page.login");
        COLLECT_PAGE=configuration.getString("user.collect");
        COLLECT_DEL=configuration.getString("user.collectdel");
        //地址
        ADDRESS_PAGE=configuration.getString("user.address.list");
        ADDRESS_ADD=configuration.getString("user.address.add");
        ADDRESS_DEL=configuration.getString("user.address.del");
        ADDRESS_UPDATE=configuration.getString("user.address.update");


        COUPON_PAGE=configuration.getString("user.coupons");
        ORDER_PAGE=configuration.getString("user.order.all");
        ORDER_CANCEL=configuration.getString("user.order.cancel");
        ORDER_DEL=configuration.getString("user.order.del");

        PIN_LIST=configuration.getString("user.pin.list");
        PIN_ACTIVITY=configuration.getString("user.pin.activity");
        PIN_ORDER_DETAIL=configuration.getString("user.pin.detail");


        //注册与密码
        PHONE_VERIFY = configuration.getString("user.phone.verify");
        PHONE_CODE = configuration.getString("user.phone.code");
        REGISTER_PAGE = configuration.getString("user.page.register");
        RESET_PASSWORD = configuration.getString("user.reset.password ");
        //用户信息
        USER_INFO = configuration.getString("user.get.info");
        USER_UPDATE = configuration.getString("user.page.update");


        ITEM_PAGE = configuration.getString("products.page.item");
        THEME_PAGE = configuration.getString("products.page.theme");
        PIN_PAGE = configuration.getString("products.page.pin");
        SUBJECT_PAGE = configuration.getString("products.page.subject");

        IMAGE_CODE =configuration.getString("user.page.imagecode");




        /******** 购物相关 ********/
        SHOPPING_LIST = configuration.getString("shopping.page.cart.list");











    }
}
