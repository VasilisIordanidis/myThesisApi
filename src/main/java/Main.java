import domain.application.AttractionApplicationService;
import domain.application.UserApplicationService;
import domain.model.AccountRepository;
import domain.model.AccountViewRepository;
import domain.model.AttractionRepository;
import infrastructure.*;
import io.vertx.core.Vertx;
public class Main {
    public static void main(String[] args) {
        System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");
        System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        Vertx vertx = Vertx.vertx();

        AccountRepository accountRepository = new PostgresAccountRepository();
        AttractionRepository attractionRepository=new PostgresAttractionRepository();
        AccountViewRepository accountViewRepository = new PostgresAttractionViewRepository();

        AttractionApplicationService attractionApplicationService  = new AttractionApplicationService(attractionRepository,accountRepository);
        UserApplicationService userApplicationService  = new UserApplicationService(accountRepository,accountViewRepository);

        vertx.deployVerticle(new HttpServerVerticle(userApplicationService,attractionApplicationService));
        vertx.deployVerticle(new ProxyServerVerticle());
    }
}