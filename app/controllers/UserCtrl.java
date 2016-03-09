package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by howen on 16/3/9.
 */
public class UserCtrl extends Controller {

    //收货地址管理
    public Result addressManagement(){
        return ok(views.html.address.render());
    }
    //创建新的收货地址
    public Result addressNew(){
        return ok(views.html.addressnew.render());
    }
}
