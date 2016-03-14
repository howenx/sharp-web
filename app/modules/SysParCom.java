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

    public static String REGIST_VARIFY;//用户注册手机号检测
    public static String REGIST_CODE;//用户注册发送短信验证码
    public static String REGIST_PAGE;//用户注册

    public static String ITEM_PAGE;
    public static String THEME_PAGE;
    public static String PIN_PAGE;








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

        REGIST_VARIFY = configuration.getString("user.page.regist.verify");
        REGIST_CODE = configuration.getString("user.page.regist.code");
        REGIST_PAGE = configuration.getString("user.page.regist");
        ITEM_PAGE = configuration.getString("products.page.item");
        THEME_PAGE = configuration.getString("products.page.theme");
        PIN_PAGE = configuration.getString("products.page.pin");






        /******** 购物相关 ********/
        SHOPPING_LIST = configuration.getString("shopping.page.cart.list");











    }
}
