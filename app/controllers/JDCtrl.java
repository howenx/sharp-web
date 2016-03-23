package controllers;

import filters.UserAuth;
import filters.VerboseAction;
import modules.SysParCom;
import play.Logger;
import play.mvc.*;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * 测试
 * Created by sibyl.sun on 16/3/19.
 */
public class JDCtrl extends Controller {
    @Inject
    ComCtrl comCtrl;

    @Security.Authenticated(UserAuth.class)
    @With(VerboseAction.class)
    public Result cashDesk(Long orderId){
        String [] header = {session().get("id-token")};

        request().headers().put("id-token", header);

        Logger.info("则你们->>>>>>>>>>>>>>>>"+session().get("id-token") +"   "+ header[0]+"   " + Arrays.toString(request().headers().get("id-token")));
        return redirect(SysParCom.PAY_ORDER+orderId);
    }

}
