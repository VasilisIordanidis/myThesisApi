package infrastructure;

import domain.model.Account;
import domain.model.AccountRepository;
import domain.model.Attraction;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.HashSet;
import java.util.Set;

public class PostgresAccountRepository implements AccountRepository {
    private final Vertx vertx = Vertx.vertx();
    //private final PoolOptions poolOptions = new PoolOptions();
    private final JsonObject config = new JsonObject()
            .put("url", "jdbc:postgresql://localhost/myThesisDB")//  allazei apo 0.2 se 0.3// 172.17.0.2:5432
            .put("user", "postgres")
            .put("password", "postgres");
    private final JDBCPool pool = JDBCPool.pool(vertx, config);

    @Override
    public Maybe<Account> logIn(String username, String password) {
        return Maybe.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM myThesisDB.public.Account WHERE username = ? AND password = ?")
                        .execute(Tuple.of(username, password))
                        .onSuccess(rows -> {
                            if (rows.size() == 1) {
                                Account account = new Account();
                                for (Row row : rows) {
                                    account.setUuid(row.getString("id"));
                                    account.setUsername(row.getString("username"));
                                    account.setPassword(row.getString("password"));
                                }
                                emitter.onSuccess(account);
                            } else {
                                emitter.onComplete();
                            }
                            connection.close();
                        }))
        );
    }

//    @Override
//    public Completable logOut() {
//        return Completable.create(emitter -> pool.getConnection()
//                .onFailure(error -> {
//                    System.out.println(error.getCause().getMessage());
//                    emitter.onError(error);
//                })
//                .onSuccess(connection -> emitter.onComplete()));
//    }

    @Override
    public Completable createAccount(String name, String surname, String email, String id, String username, String password) {
        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("INSERT INTO myThesisDB.public.User (first_name, last_name, email, account_id) VALUES (?, ?, ?, ?)")
                        .execute(Tuple.of(name, surname, email, id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(dummy -> {
                            connection.preparedQuery("INSERT INTO myThesisDB.public.Account (id, username, password) VALUES (?, ?, ?)")
                                    .execute(Tuple.of(id, username, password))
                                    .onFailure(error -> {
                                        System.out.println(error.getCause().getMessage());
                                        emitter.onError(error);
                                        connection.close();
                                    })
                                    .onSuccess(dummy1 -> {
                                        emitter.onComplete();
                                        connection.close();
                                    });
                        })
                )
        );
    }

    @Override
    public Completable deleteAccount(String id) {
        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("DELETE FROM myThesisDB.public.User WHERE account_id=?")
                        .execute(Tuple.of(id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            if (rows.rowCount() > 0) {
                                connection.preparedQuery("DELETE FROM myThesisDB.public.Account WHERE id=?")
                                        .execute(Tuple.of(id))
                                        .onFailure(error -> {
                                            System.out.println(error.getCause().getMessage());
                                            emitter.onError(error);
                                        })
                                        .onSuccess(rows1 -> {
                                            if (rows1.rowCount() > 0) {
                                                connection.preparedQuery("DELETE FROM myThesisDB.public.Attraction WHERE account_id=?")
                                                        .execute(Tuple.of(id))
                                                        .onFailure(error -> {
                                                            System.out.println(error.getCause().getMessage());
                                                            emitter.onError(error);
                                                        })
                                                        .onSuccess(rows2 -> {
                                                            emitter.onComplete();
                                                        });
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
    public Maybe<Account> getAccountById(String id) {
        return Maybe.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM myThesisDB.public.Account WHERE myThesisDB.public.Account.id  ?")
                        .execute(Tuple.of(id))
                        .onFailure(error -> {
                            System.out.println(error.getCause().getMessage());
                            emitter.onError(error);
                        })
                        .onSuccess(rows -> {
                            if (rows.size() > 0) {
                                Account account = new Account();
                                for (Row row : rows) {
                                    account.setUuid(row.getString("id"));
                                    account.setUsername(row.getString("username"));
                                    account.setPassword(row.getString("password"));
                                }
                                emitter.onSuccess(account);
                            } else {
                                emitter.onComplete();
                            }
                        })
                )
        );
    }
}
