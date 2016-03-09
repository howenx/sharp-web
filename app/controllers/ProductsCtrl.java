package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * 主题,产品相关
 * Created by howen on 16/3/9.
 */
public class ProductsCtrl extends Controller {
    //商品明细
    public Result detail(){return ok( views.html.detail.render());}


}
