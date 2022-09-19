import domain.application.AttractionApplicationService;
import domain.application.UploadsApplicationService;
import domain.application.UserApplicationService;
import domain.model.AccountRepository;
import domain.model.AccountViewRepository;
import domain.model.AttractionRepository;
import domain.model.UploadRepository;
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
        UploadRepository uploadRepository = new PostgresUploadsRepository();

        AttractionApplicationService attractionApplicationService  = new AttractionApplicationService(attractionRepository,accountRepository);
        UserApplicationService userApplicationService  = new UserApplicationService(accountRepository,accountViewRepository);
        UploadsApplicationService uploadsApplicationService = new UploadsApplicationService(uploadRepository);
        UploadsApplicationService applicationService = new UploadsApplicationService(uploadRepository);
        vertx.deployVerticle(new HttpServerVerticle(userApplicationService,attractionApplicationService,uploadsApplicationService));
        vertx.deployVerticle(new ProxyServerVerticle());
    }
}