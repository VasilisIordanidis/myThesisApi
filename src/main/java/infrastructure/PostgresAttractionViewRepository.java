package infrastructure;

import domain.model.AccountView;
import domain.model.AccountViewRepository;
import domain.model.Attraction;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.UUID;

public class PostgresAttractionViewRepository implements AccountViewRepository {
    private final Vertx vertx = Vertx.vertx();
    //private final PoolOptions poolOptions = new PoolOptions();
    private final JsonObject config = new JsonObject()
            .put("url", "jdbc:postgresql://localhost/myThesisDB")//  allazei apo 0.2 se 0.3// 172.17.0.2:5432
            .put("user", "postgres")
            .put("password", "postgres");
    private final JDBCPool pool = JDBCPool.pool(vertx, config);
    public PostgresAttractionViewRepository() {
    }
    @Override
    public Single<AccountView> getAccountDetails(String id){
        return Single.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM public.\"Account\" WHERE public.\"Account\".id = ? ")
                        .execute(Tuple.of(UUID.fromString(id)))
                        .onFailure(error ->{
                            //System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            AccountView accountView = new AccountView();
                            for(Row row : rows){
                                accountView.setUsername(row.getString("username"));
//                                Attraction attraction = new Attraction(
//                                        row.getString("name"),
//                                        row.getInteger("total_reviews"),
//                                        row.getDouble("rating"),
//                                        row.getString("url"),
//                                        row.getString("address"));
//                                accountView.getAttractionSet().add(attraction);
//                                accountView.setUsername(row.getString("username"));
                                connection.preparedQuery("SELECT * FROM public.\"Attraction\" WHERE public.\"Attraction\".account_id = ?")
                                        .execute(Tuple.of(UUID.fromString(id)))
                                        .onFailure(error -> {
                                            emitter.onError(error);
                                            connection.close();
                                        })
                                        .onSuccess(attractionRows -> {
                                            for(Row attractionRow : attractionRows){
                                                Attraction attraction = new Attraction(
                                                    attractionRow.getString("name"),
                                                    attractionRow.getInteger("total_reviews"),
                                                    attractionRow.getDouble("rating"),
                                                    attractionRow.getString("url"),
                                                    attractionRow.getString("address"));
                                                accountView.getAttractionSet().add(attraction);
                                            }
                                            emitter.onSuccess(accountView);
                                            connection.close();
                                        });
                            }

                        })
                )
        );
    }
}
