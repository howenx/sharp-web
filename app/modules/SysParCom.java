package modules;

import com.squareup.okhttp.OkHttpClient;
import play.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

/**
 * 查询参数表中的参数项
 * Created by hao on 16/2/28.
 */
@Singleton
public class SysParCom {
    public static String INDEX_PAGE;
    public static final OkHttpClient client = new OkHttpClient();
    public static String LOGIN_PAGE;
<<<<<<< HEAD
    public static String COLLECT_PAGE;
    public static String ADDRESS_PAGE;//地址

=======

    public static String REGIST_VARIFY;//用户注册手机号检测
    public static String REGIST_CODE;//用户注册发送短信验证码
    public static String REGIST_PAGE;//用户注册

    public static String ITEM_PAGE;
    public static String IHEME_PAGE;
    public static String PIN_PAGE;
>>>>>>> a649c06ed5a23c8e2a72f1828de1415b0d86790d
    @Inject
    public SysParCom(Configuration configuration) {

        INDEX_PAGE = configuration.getString("products.page.index");
        LOGIN_PAGE = configuration.getString("user.page.login");
<<<<<<< HEAD
        COLLECT_PAGE=configuration.getString("user.collect");
        ADDRESS_PAGE=configuration.getString("user.address");
=======
        REGIST_VARIFY = configuration.getString("user.page.regist.verify");
        REGIST_CODE = configuration.getString("user.page.regist.code");
        REGIST_PAGE = configuration.getString("user.page.regist");
        ITEM_PAGE = configuration.getString("products.page.item");
        IHEME_PAGE = configuration.getString("products.page.theme");
        PIN_PAGE = configuration.getString("products.page.pin");
>>>>>>> a649c06ed5a23c8e2a72f1828de1415b0d86790d
    }
}
