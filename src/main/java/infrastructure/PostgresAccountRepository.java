package infrastructure;

import domain.model.Account;
import domain.model.AccountRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.UUID;

public class PostgresAccountRepository implements AccountRepository {
    private final Vertx vertx = Vertx.vertx();
    //private final PoolOptions poolOptions = new PoolOptions();
    private final JsonObject config = new JsonObject()
            .put("url", "jdbc:postgresql://localhost:5432/myThesisDB")//  allazei apo 0.2 se 0.3// 172.17.0.2:5432
            .put("user", "postgres")
            .put("password", "postgres");
    private final JDBCPool pool = JDBCPool.pool(vertx, config);

    @Override
    public Single<Account> logIn(String username, String password) {
        return Single.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM public.\"Account\" WHERE public.\"Account\".\"username\" = ? AND public.\"Account\".\"password\" = ?")
                        .execute(Tuple.of(username, password))
                        .onFailure(error -> {
                            System.out.println(error.getCause().toString());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            if (rows.size() == 1) {
                                Account account = new Account();
                                for (Row row : rows) {
                                    account.setUuid(row.getString("id"));
                                    account.setUsername(row.getString("username"));
                                    account.setPassword(row.getString("password"));
                                }
                                emitter.onSuccess(account);
                            }
                            else if(rows.size()==0){
                                emitter.onError(new Throwable("Wrong username or password"));
                            }
                            connection.close();
                        }))
        );
    }

    @Override
    public Completable createAccount(String name, String surname, String email, UUID id, String username, String password) {
        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("INSERT INTO public.\"User\" (first_name, last_name, email, account_id) VALUES (?, ?, ?, ?)")
                        .execute(Tuple.of(name, surname, email, id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(dummy -> connection.preparedQuery("INSERT INTO public.\"Account\" (id, username, password) VALUES (?, ?, ?)")
                                .execute(Tuple.of(id, username, password))
                                .onFailure(error -> {
                                    System.out.println(error.getCause().getMessage());
                                    emitter.onError(error);
                                    connection.close();
                                })
                                .onSuccess(dummy1 -> {
                                    emitter.onComplete();
                                    connection.close();
                                }))
                )
        );
    }

    @Override
    public Completable deleteAccount(UUID id) {
        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("DELETE FROM public.\"User\" WHERE account_id=?")
                        .execute(Tuple.of(id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            if (rows.rowCount() > 0) {
                                connection.preparedQuery("DELETE FROM public.\"Account\" WHERE id=?")
                                        .execute(Tuple.of(id))
                                        .onFailure(error -> {
                                            System.out.println(error.getCause().getMessage());
                                            emitter.onError(error);
                                        })
                                        .onSuccess(rows1 -> {
                                            if (rows1.rowCount() > 0) {
                                                connection.preparedQuery("DELETE FROM public.\"Attraction\" WHERE account_id=?")
                                                        .execute(Tuple.of(id))
                                                        .onFailure(error -> {
                                                            System.out.println(error.getCause().getMessage());
                                                            emitter.onError(error);
                                                        })
                                                        .onSuccess(rows2 -> emitter.onComplete());
                                            } else {
                                                emitter.onError(new Throwable("error at delete account"));
                                            }
                                        });
                            }
                        })
                )
        );
    }

    @Override
    public Maybe<Account> getAccountById(UUID id) {
        return Maybe.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM public.\"Account\" WHERE public.\"Account\".id = ?")
                        .execute(Tuple.of(id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            if (rows.size() > 0) {
                                Account account = new Account();
                                for (Row row : rows) {
                                    account.setUuid(String.valueOf(row.getUUID("id")));
                                    account.setUsername(row.getString("username"));
                                    account.setPassword(row.getString("password"));
                                }
                                emitter.onSuccess(account);
                            } else {
                                emitter.onComplete();
                            }
                            connection.close();
                        })
                )
        );
    }
}
