package controllers;

import play.mvc.Controller;

import play.mvc.Result;


/**
 * 用户相关
 * Created by howen on 16/3/9.
 */
public class UserCtrl extends Controller {
    //收货地址
    public Result address(){
         return ok(views.html.address.render());
    }
    //创建新的收货地址
    public Result addressnew(){
        return ok(views.html.addressnew.render());
    }
    //身份认证
    public Result carded(){
        return ok(views.html.carded.render());
    }


}
