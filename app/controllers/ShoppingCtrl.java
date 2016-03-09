package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * 购物车,结算相关
 * Created by howen on 16/3/9.
 */
public class ShoppingCtrl extends Controller {

    public Result setting() {
        return ok(views.html.setting.render());
    }
    public Result pinList(){return ok( views.html.pinList.render());}
    public Result login(){return ok( views.html.login.render());}
    public Result logistic(){return ok( views.html.logistics.render());}
    public Result means(){return ok( views.html.means.render());}
    public Result myView(){return ok( views.html.my.render());}
    public Result navBar(){return ok( views.html.nav.render());}
    public Result obligati(){return ok( views.html.obligation.render());}
    public Result orders(){return ok( views.html.orders.render());}
    public Result resetPasswd(){return ok( views.html.resetPasswd.render());}
    public Result settle(){return ok( views.html.settle.render());}
    public Result regist(){return ok( views.html.regist.render());}
    public Result tickling(){return ok( views.html.tickling.render());}
    public Result cart(){return ok( views.html.cart.render());}
    //全部订单
    public Result all(){return ok( views.html.all.render());}
    //待评价订单
    public Result appraise(){return ok( views.html.appraise.render());}
    //发表评价
    public Result assess(){return ok( views.html.assess.render());}
    //优惠券
    public Result coupon(){return ok( views.html.coupon.render());}
    //待发货
    public Result delivered(){return ok( views.html.delivered.render());}
    //退款
    public Result drawback(){return ok( views.html.drawback.render());}
    //我的拼团
    public Result fightgroups(){return ok( views.html.fightgroups.render());}


}
