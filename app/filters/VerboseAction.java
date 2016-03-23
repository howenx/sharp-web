package filters;

import play.api.http.MediaRange;
import play.api.mvc.Request;
import play.api.mvc.RequestHeader;
import play.i18n.Lang;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;
import java.util.Map;

/**
 * action
 * Created by howen on 16/3/22.
 */
public class VerboseAction extends play.mvc.Action.Simple {
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {
        Http.Request re =

        new Http.Request() {
            @Override
            public Http.RequestBody body() {
                return null;
            }

            @Override
            public String username() {
                return null;
            }

            @Override
            public void setUsername(String username) {

            }

            @Override
            public Http.Request withUsername(String username) {
                return null;
            }

            @Override
            public Request<Http.RequestBody> _underlyingRequest() {
                return null;
            }

            @Override
            public String uri() {
                return ctx.request().uri();
            }

            @Override
            public String method() {
                return ctx.request().method();
            }

            @Override
            public String version() {
                return ctx.request().version();
            }

            @Override
            public String remoteAddress() {
                return ctx.request().remoteAddress();
            }

            @Override
            public boolean secure() {
                return ctx.request().secure();
            }

            @Override
            public String host() {
                return ctx.request().host();
            }

            @Override
            public String path() {
                return ctx.request().path();
            }

            @Override
            public List<Lang> acceptLanguages() {
                return ctx.request().acceptLanguages();
            }

            @Override
            public List<MediaRange> acceptedTypes() {
                return ctx.request().acceptedTypes();
            }

            @Override
            public boolean accepts(String mimeType) {
                return ctx.request().accepts(mimeType);
            }

            @Override
            public Map<String, String[]> queryString() {
                return ctx.request().queryString();
            }

            @Override
            public String getQueryString(String key) {
                return ctx.request().getQueryString(key);
            }

            @Override
            public Http.Cookies cookies() {
                return ctx.request().cookies();
            }

            @Override
            public Http.Cookie cookie(String name) {
                return ctx.request().cookie(name);
            }

            @Override
            public Map<String, String[]> headers() {
                Map<String, String[]> pp =  ctx.request().headers();
                String[] tt = {ctx.session().get("id-token")};
                pp.put("id-token",tt);
                return pp;
            }

            @Override
            public String getHeader(String headerName) {
                return ctx.request().getHeader(headerName);
            }

            @Override
            public boolean hasHeader(String headerName) {
                return ctx.request().hasHeader(headerName);
            }

            @Override
            public RequestHeader _underlyingHeader() {
                return ctx.request()._underlyingHeader();
            }
        };
        return delegate.call(ctx.withRequest(re));
    }
}