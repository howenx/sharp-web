package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Created by howen on 16/3/9.
 */
public class ShoppingCtrl extends Controller {
    //全部订单
    public Result allOrder(){
        return ok(views.html.all.render());
    }
    //待评价订单
    public Result appraiseOrder(){
        return ok(views.html.appraise.render());
    }

}
