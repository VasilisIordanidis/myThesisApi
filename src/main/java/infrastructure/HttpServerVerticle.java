package infrastructure;

import domain.application.AttractionApplicationService;
import domain.application.UserApplicationService;
import domain.application.command.AddAttractionCommand;
import domain.application.command.CreateUserCommand;
import domain.application.command.DeleteUserCommand;
import domain.application.command.RemoveAttractionCommand;
import domain.application.query.GetSavedAttractionsQuery;
import domain.application.query.LogInQuery;
import domain.model.Attraction;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

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
        router.route().handler(BodyHandler.create());
        router.get("/api/user").consumes("*/json").produces("*/json").handler(context -> {
            context.response().setChunked(true);
            String username = context.request().getParam("username");
            String password = context.request().getParam("password");
            System.out.println(username + password);
            LogInQuery logInQuery = new LogInQuery(username, password);
            userApplicationService.execute(logInQuery).subscribe(
                    accountView -> {
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.put("username",accountView.getUsername());
                        JsonArray jsonArray = new JsonArray();
                        for(Attraction attraction : accountView.getAttractionSet()){
                            JsonObject attractionJson = new JsonObject().put("name",attraction.getName())
                                    .put("address",attraction.getAddress())
                                    .put("total_reviews",attraction.getTotalReviews())
                                    .put("rating",attraction.getRating())
                                    .put("photo",attraction.getImage());
                            jsonArray.add(attractionJson);
                        }
                        jsonObject.put("attractions",jsonArray);
                        System.out.println("GET login");
                        context.response().write(jsonObject.toString());
                        context.response().end();

                    },
                    onError -> {
                        System.out.println("GET LOGIN error");
                        context.response().write(onError.getMessage());
                        context.response().end();

                    }
            );
        });
        router.get("/api/user/:id/attractions").consumes("*/json").produces("*/json").handler(context -> {
            context.response().setChunked(true);
            GetSavedAttractionsQuery query = new GetSavedAttractionsQuery(context.pathParam("id"));
            attractionApplicationService.execute(query).subscribe(
                    attractions -> {
                        JsonArray array = new JsonArray();
                        for(Attraction attraction : attractions){
                            JsonObject attractionJson = new JsonObject().put("name",attraction.getName())
                                    .put("address",attraction.getAddress())
                                    .put("total_reviews",attraction.getTotalReviews())
                                    .put("rating",attraction.getRating())
                                    .put("photo",attraction.getImage());
                            array.add(attractionJson);
                        }
                        JsonObject res = new JsonObject().put("attractions",array);
                        context.response().write(res.toString());
                        context.response().end();
                    },
                    onError -> {
                        context.response().write(onError.getMessage());
                        context.response().end();
                    },
                    () -> {
                        context.response().end();
                    }
            );
        });

        router.post("/api/user").consumes("*/json").produces("*/json").handler(context -> {
            context.response().setChunked(true);
            JsonObject jsonObject = context.getBodyAsJson();
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            String email = jsonObject.getString("email");
            //String id = jsonObject.getString("id");
            String username = jsonObject.getString("username");
            String password = jsonObject.getString("password");
            CreateUserCommand createUserCommand = new CreateUserCommand(firstName,lastName,email,username,password);
            userApplicationService.execute(createUserCommand).subscribe(
                    () -> {
                        context.response().end();
                    },
                    onError -> {
                        context.response().write(onError.getMessage());
                        context.response().end();
                    }
            );
        });

        router.post("/api/user/:id/attractions").consumes("*/json").produces("*/json").handler(context -> {
            context.response().setChunked(true);
            JsonObject jsonObject = context.getBodyAsJson();
            String name = jsonObject.getString("name");
            String address = jsonObject.getString("address");
            int totalReviews = jsonObject.getInteger("totalReviews");
            double rating = jsonObject.getDouble("rating");
            String url = jsonObject.getString("url");
            String password = jsonObject.getString("password");
            String id = context.pathParam("id");
            AddAttractionCommand addAttractionCommand = new AddAttractionCommand(id,name,address,rating,totalReviews,url);
            attractionApplicationService.execute(addAttractionCommand).subscribe(
                    () -> context.response().end(),
                    onError -> {
                        context.response().write(onError.getMessage());
                        context.response().end();
                    }
            );
        });

        router.delete("/api/user/:id").consumes("*/json").produces("*/json").handler(context -> {
            context.response().setChunked(true);
            String id = context.pathParam("id");
            DeleteUserCommand deleteUserCommand = new DeleteUserCommand(id);
            userApplicationService.execute(deleteUserCommand).subscribe(
                    () -> context.response().end(),
                    onError -> {
                        context.response().setStatusCode(404);
                        context.response().write(onError.getMessage());
                        context.response().end();
                    }
            );
        });

        router.delete("/api/user/:id/attractions").consumes("*/json").produces("*/json").handler(context -> {
            context.response().setChunked(true);
            JsonObject jsonObject = context.getBodyAsJson();
            String id = context.pathParam("id");
            String name = jsonObject.getString("name");
            String address = jsonObject.getString("address");
            RemoveAttractionCommand removeAttractionCommand = new RemoveAttractionCommand(id, name, address);
            attractionApplicationService.execute(removeAttractionCommand).subscribe(
                    () -> context.response().end(),
                    onError -> {
                        context.response().setStatusCode(404);
                        context.response().write(onError.getMessage());
                        context.response().end();
                    }
            );
        });

        server.requestHandler(router).listen(8080, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("server is listening");
            } else {
                System.out.println("failed to bind :" + res.cause());
            }
        }); //port 8080 & host 0.0.0.0
    }

    @Override
    public void stop() {
        server.close(res -> {
            if (res.succeeded()) {
                System.out.println("Server closed");
            } else {
                System.out.println("Error");
            }
        });
    }
}
