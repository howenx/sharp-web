package controllers;

import play.mvc.Controller;
import play.mvc.Result;
/**
 * Modified by Sunny Wu 16/1/19
 */
public class Application extends Controller {

    //图片服务器url
    public static final String IMAGE_URL = play.Play.application().configuration().getString("image.server.url");

    //发布服务器url
    public static final String DEPLOY_URL = play.Play.application().configuration().getString("deploy.server.url");

    //首页
    public Result index() {
        return ok(views.html.index.render());
    }
}
