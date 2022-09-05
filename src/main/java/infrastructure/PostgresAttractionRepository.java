package infrastructure;

import domain.model.Attraction;
import domain.model.AttractionRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PostgresAttractionRepository implements AttractionRepository {
    private final Vertx vertx = Vertx.vertx();
    //private final PoolOptions poolOptions = new PoolOptions();
    private final JsonObject config = new JsonObject()
            .put("url", "jdbc:postgresql://localhost/myThesisDB")//  allazei apo 0.2 se 0.3// 172.17.0.2:5432
            .put("user", "postgres")
            .put("password", "postgres");
    private final JDBCPool pool = JDBCPool.pool(vertx, config);

    @Override
    public Single<Set<Attraction>> getSavedAttractions(String id){
        return Single.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM public.\"Attraction\" WHERE public.\"Attraction\".\"account_id\" = ?")
                        .execute(Tuple.of(UUID.fromString(id)))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            Set<Attraction> attractionSet = new HashSet<>();
                            for(Row row : rows){
                                Attraction attraction = new Attraction();
                                attraction.setName(row.getString("name"));
                                attraction.setRating(row.getDouble("rating"));
                                attraction.setTotalReviews(row.getInteger("total_reviews"));
                                attraction.setImage(row.getString("photo_url"));
                                attraction.setAddress(row.getString("address"));
                                attractionSet.add(attraction);
                            }
                            emitter.onSuccess(attractionSet);
                            connection.close();
                        })
                )
        );
    }

    @Override
    public Completable addAttraction(String id, String name, Double rating, int total_reviews, String url, String address){
        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> {
                    connection.preparedQuery("INSERT INTO public.\"Attraction\" (account_id,name,rating,total_reviews,photo_url,address) VALUES (?, ?, ?, ?, ?, ?)")
                            .execute(Tuple.of(UUID.fromString(id), name, rating, total_reviews, url, address))
                            .onFailure(error -> {
                                System.out.println(error);
                                emitter.onError(error);
                                connection.close();
                            })
                            .onSuccess(rows -> {
                               if(rows.rowCount() > 0){
                                   emitter.onComplete();
                               }
                               else{
                                   emitter.onError(new Throwable("Error at insert"));
                               }
                               connection.close();
                            });

                }));
    }

    @Override
    public Completable removeAttraction(String id, String name, String address){

        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("DELETE FROM public.\"Attraction\" WHERE public.\"Attraction\".\"account_id\"=? AND public.\"Attraction\".\"name\"=? AND public.\"Attraction\".\"address\" = ?")
                        .execute(Tuple.of(UUID.fromString(id), name, address))
                        .onFailure(error -> {
                            System.out.println(error.getCause());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            if(rows.rowCount()>0){
                                emitter.onComplete();
                            }else {
                                emitter.onError(new Throwable("error at delete"));
                            }
                            connection.close();
                        })
                )
        );
    }
}
