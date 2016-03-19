package controllers;

import filters.UserAuth;
import modules.SysParCom;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;

/**
 * Created by sibyl.sun on 16/3/19.
 */
public class JDCtrl extends Controller {
    @Inject
    ComCtrl comCtrl;

    @Security.Authenticated(UserAuth.class)
    public F.Promise<Result> cashDesk(Long orderId){
        return comCtrl.getReqReturnMsg(SysParCom.PAY_ORDER+orderId);

    }

}
