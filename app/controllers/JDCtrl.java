package controllers;

import filters.UserAuth;
import modules.SysParCom;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

/**
 * 测试
 * Created by sibyl.sun on 16/3/19.
 */
public class JDCtrl extends Controller {


    @Security.Authenticated(UserAuth.class)
    public Result cashDesk(Long orderId) {
        return redirect(SysParCom.PAY_ORDER + orderId).withHeader("id-token", session().get("id-token"));
    }

}
