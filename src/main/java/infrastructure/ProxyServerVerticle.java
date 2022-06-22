package infrastructure;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.CookieSameSite;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.httpproxy.HttpProxy;

public class ProxyServerVerticle extends AbstractVerticle {
    private final Vertx vertx = Vertx.vertx();
    HttpClient proxyClient = vertx.createHttpClient();
    private final HttpProxy apiProxy = HttpProxy.reverseProxy(proxyClient);
    private final HttpProxy webProxy = HttpProxy.reverseProxy(proxyClient);
    private final HttpServer proxyServer = vertx.createHttpServer();

    public void start() {
        apiProxy.origin(8080, "localhost");
        webProxy.origin(4200, "localhost");
        //generate session
        SessionStore sessionStore = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(sessionStore);
        sessionHandler.setSessionCookieName("session_id")
                .setCookieSameSite(CookieSameSite.STRICT)
                .setCookieHttpOnlyFlag(true);

        Router router = Router.router(vertx);
        router.route().handler(sessionHandler);
        //HttpRequest
        router.routeWithRegex("/api/.*").handler(ProxyHandler.create(apiProxy))
                .handler(context -> {
                    context.request().headers().set("userID", context.session().id());
                    context.next();
                });

        router.routeWithRegex("/web/?.*").handler(ProxyHandler.create(webProxy));

        proxyServer.requestHandler(router).listen(7070, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("proxy is listening");
            } else {
                System.out.println("failed to bind :" + res.cause());
            }
        });
    }

    public void stop() {
        proxyServer.close(res -> {
            if (res.succeeded()) {
                System.out.println("Proxy closed");
            } else {
                System.out.println("Error");
            }
        });
    }
}
