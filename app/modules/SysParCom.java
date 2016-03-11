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
    public static String REGIST_VARIFY;//用户注册手机号检测
    public static String REGIST_CODE;//用户注册发送短信验证码
    public static String REGIST_PAGE;//用户注册
    @Inject
    public SysParCom(Configuration configuration) {

        INDEX_PAGE = configuration.getString("products.page.index");
        LOGIN_PAGE = configuration.getString("user.page.login");
        REGIST_VARIFY = configuration.getString("user.page.regist.verify");
        REGIST_CODE = configuration.getString("user.page.regist.code");
        REGIST_PAGE = configuration.getString("user.page.regist");
    }
}
