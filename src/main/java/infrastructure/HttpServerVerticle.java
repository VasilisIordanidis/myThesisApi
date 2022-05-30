package infrastructure;

import domain.application.AttractionApplicationService;
import domain.application.UserApplicationService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

public class HttpServerVerticle extends AbstractVerticle {
    private final Vertx vertx = Vertx.vertx();
    private final HttpServer server = vertx.createHttpServer();
    private final Router router = Router.router(vertx);
    private final UserApplicationService userApplicationService;
    private final AttractionApplicationService attractionApplicationService;

    public HttpServerVerticle(UserApplicationService userApplicationService, AttractionApplicationService attractionApplicationService) {
        this.userApplicationService = userApplicationService;
        this.attractionApplicationService = attractionApplicationService;
    }

    @Override
    public void start()  {

    }

    @Override
    public void stop() {

    }
}
