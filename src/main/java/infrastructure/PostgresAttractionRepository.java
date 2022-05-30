package infrastructure;

import domain.model.Attraction;
import domain.model.AttractionRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.HashSet;
import java.util.Set;

public class PostgresAttractionRepository implements AttractionRepository {
    private final Vertx vertx = Vertx.vertx();
    //private final PoolOptions poolOptions = new PoolOptions();
    private final JsonObject config = new JsonObject()
            .put("url", "jdbc:postgresql://localhost/myThesisDB")//  allazei apo 0.2 se 0.3// 172.17.0.2:5432
            .put("user", "postgres")
            .put("password", "postgres");
    private final JDBCPool pool = JDBCPool.pool(vertx, config);

    @Override
    public Maybe<Set<Attraction>> getSavedAttractions(String id){
        return Maybe.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM myThesisDB.public.Attraction WHERE account_id = ?")
                        .execute(Tuple.of(id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            if(rows.size()>0){
                                Set<Attraction> attractionSet = new HashSet<>();
                                for(Row row : rows){
                                    Attraction attraction = new Attraction();
                                    attraction.setName(row.getString("name"));
                                    attraction.setRating(row.getDouble("rating"));
                                    attraction.setTotalReviews(row.getInteger("total_reviews"));
                                    attraction.setImage(row.getString("image_url"));
                                    attraction.setAddress(row.getString("address"));
                                    attractionSet.add(attraction);
                                }
                                emitter.onSuccess(attractionSet);
                            } else {
                                emitter.onComplete();
                            }
                            connection.close();
                        })
                )
        );
    }

    @Override
    public Completable addAttraction(){
        return Completable.create(emitter -> {});
    }

    @Override
    public Completable removeAttraction(){
        return Completable.create(emitter -> {});
    }
}
