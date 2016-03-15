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
    public static String INDEX_PAGE;
    public static final OkHttpClient client = new OkHttpClient();
    public static String LOGIN_PAGE;
    public static String COLLECT_PAGE;
    public static String COLLECT_DEL; //取消收藏
    public static String ADDRESS_PAGE;//地址
    public static String ADDRESS_ADD;//地址新增
    public static String COUPON_PAGE;//我的优惠券
    public static String ORDER_PAGE;//我的订单





    public static String REGIST_VARIFY;//用户注册手机号检测
    public static String REGIST_CODE;//用户注册发送短信验证码
    public static String REGIST_PAGE;//用户注册
    public static String RESET_VERIFY;//忘记密码手机号检测
    public static String RESET_PASSWORD;//密码修改
    public static String USER_UPDATE;//个人信息修改

    public static String ITEM_PAGE;//普通商品,多样化价格商品
    public static String THEME_PAGE;//主题
    public static String PIN_PAGE;//拼购商品
    public static String SUBJECT_PAGE;//主题中自定义价格商品


    public static String IMAGE_CODE;//图形验证码







    /******** 购物相关 ********/
    public static  String SHOPPING_LIST;//购物车列表











    @Inject
    public SysParCom(Configuration configuration) {

        INDEX_PAGE = configuration.getString("products.page.index");
        LOGIN_PAGE = configuration.getString("user.page.login");
        COLLECT_PAGE=configuration.getString("user.collect");
        COLLECT_DEL=configuration.getString("user.collectdel");
        //地址
        ADDRESS_PAGE=configuration.getString("user.address");
        ADDRESS_ADD=configuration.getString("user.addressnew");


        COUPON_PAGE=configuration.getString("user.coupons");
        ORDER_PAGE=configuration.getString("user.order.all");


        
        //注册与密码
        REGIST_VARIFY = configuration.getString("user.regist.verify");
        REGIST_CODE = configuration.getString("user.regist.code");
        REGIST_PAGE = configuration.getString("user.page.regist");
        RESET_VERIFY = configuration.getString("user.reset.verify");
        RESET_PASSWORD = configuration.getString("user.reset.password ");
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
