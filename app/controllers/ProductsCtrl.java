package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * 首页,主题,产品相关
 * Created by howen on 16/3/9.
 */
public class ProductsCtrl extends Controller {

    //首页
    public Result index() {
        return ok(views.html.products.index.render());
    }


    //商品明细
    public Result detail() {
        return ok(views.html.products.detail.render());
    }


}
