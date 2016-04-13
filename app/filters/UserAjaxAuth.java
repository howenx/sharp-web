package filters;

import domain.Message;
import modules.SysParCom;
import net.spy.memcached.MemcachedClient;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static modules.SysParCom.M_HTTP;
import static modules.SysParCom.WEIXIN_APPID;

/**
 * 用户校验 ajax 请求未登录返回的是JSON
 * Created by howen on 15/11/25.
 */
public class UserAjaxAuth extends Security.Authenticator {

    @Inject
    private MemcachedClient cache;


    @Inject
    UserAuth auth;


    @Override
    public String getUsername(Http.Context ctx) {
        return auth.getUsername(ctx);
    }

    @Override
    public Result onUnauthorized(Http.Context ctx) {

        String result = auth.getResult();
        String url;

        String state = auth.getState(ctx);

        if (result != null && result.equals("state_base")) {
            try {
                url = SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/base", "UTF-8") + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                url = "/login?state=" + state;
            }
        } else if (result != null && result.equals("bind_page")) {
            url = "/bind?state=" + state;
        } else if (result != null && result.equals("state_userinfo")) {
            try {
                url = SysParCom.WEIXIN_CODE_URL + "appid=" + WEIXIN_APPID + "&&redirect_uri=" + URLEncoder.encode(M_HTTP + "/wechat/userinfo", "UTF-8") + "&response_type=code&scope=snsapi_userinfo&state=" + state + "#wechat_redirect";
            } catch (UnsupportedEncodingException e) {
                url = "/login?state=" + state;
            }
        } else {
            url = "/login?state=" + state;
        }

        //错误id是5006,message为登录跳转的url
        Message message = new Message(url, Message.ErrorCode.USER_NOT_LOGIN.getIndex());
        return ok(Json.toJson(message));
    }
}

