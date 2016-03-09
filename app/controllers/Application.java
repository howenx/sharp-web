package controllers;

import play.mvc.Controller;
import play.mvc.Result;
/**
 * Modified by Sunny Wu 16/1/19
 */
public class Application extends Controller {

    //首页
    public Result index() {
        return ok(views.html.index.render());
    }
}
