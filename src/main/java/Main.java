import domain.application.AttractionApplicationService;
import domain.application.UserApplicationService;
import domain.model.AccountRepository;
import domain.model.AccountViewRepository;
import domain.model.AttractionRepository;
import infrastructure.*;
import io.vertx.core.Vertx;
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        AccountRepository accountRepository = new PostgresAccountRepository();
        AttractionRepository attractionRepository=new PostgresAttractionRepository();
        AccountViewRepository accountViewRepository = new PostgresAttractionViewRepository();

        AttractionApplicationService attractionApplicationService  = new AttractionApplicationService(attractionRepository,accountRepository);
        UserApplicationService userApplicationService  = new UserApplicationService(accountRepository,accountViewRepository);

        vertx.deployVerticle(new HttpServerVerticle(userApplicationService,attractionApplicationService),res ->{
            if(res.succeeded()){
                System.out.println("http server  verticle deployed");
            } else {
                System.out.println("http server did not deployed"+res.cause());
            }
        });
        vertx.deployVerticle(new ProxyServerVerticle(), res -> {
            if(res.succeeded()){
                System.out.println("proxy server  verticle deployed");
            } else {
                System.out.println("proxy server did not deployed");
            }
        });
    }
}