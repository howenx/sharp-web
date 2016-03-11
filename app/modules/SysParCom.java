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
    public static String ITEM_PAGE;
    public static String IHEME_PAGE;
    public static String PIN_PAGE;
    @Inject
    public SysParCom(Configuration configuration) {

        INDEX_PAGE = configuration.getString("products.page.index");
        LOGIN_PAGE = configuration.getString("user.page.login");
        ITEM_PAGE = configuration.getString("products.page.item");
        IHEME_PAGE = configuration.getString("products.page.theme");
        PIN_PAGE = configuration.getString("products.page.pin");
    }
}
