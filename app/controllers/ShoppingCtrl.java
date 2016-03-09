package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * 购物车,结算相关
 * Created by howen on 16/3/9.
 */
public class ShoppingCtrl extends Controller {

    //全部订单
    public Result all() {
        return ok(views.html.shopping.all.render());
    }

    //待评价订单
    public Result appraise() {
        return ok(views.html.shopping.appraise.render());
    }

    //发表评价
    public Result assess() {
        return ok(views.html.shopping.assess.render());
    }

    public Result cart() {
        return ok(views.html.shopping.cart.render());
    }

    //待发货
    public Result delivered() {
        return ok(views.html.shopping.delivered.render());
    }

    //退款
    public Result drawback() {
        return ok(views.html.shopping.drawback.render());
    }

    //我的拼团
    public Result fightgroups() {
        return ok(views.html.shopping.fightgroups.render());
    }

    public Result logistic() {
        return ok(views.html.shopping.logistics.render());
    }

    public Result obligati() {
        return ok(views.html.shopping.obligation.render());
    }

    public Result orders() {
        return ok(views.html.shopping.orders.render());
    }

    public Result pinList() {
        return ok(views.html.shopping.pinList.render());
    }

    public Result settle() {
        return ok(views.html.shopping.settle.render());
    }
}
