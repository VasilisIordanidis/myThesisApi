package infrastructure;

import com.mchange.io.FileUtils;
import domain.model.UploadRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PostgresUploadsRepository implements UploadRepository {
    private final Vertx vertx = Vertx.vertx();
    private final JsonObject config = new JsonObject()
            .put("url", "jdbc:postgresql://localhost/myThesisDB")
            .put("user", "postgres")
            .put("password", "postgres");

    private final JDBCPool pool = JDBCPool.pool(vertx, config);
    @Override
    public Completable addUpload(String id,String fileData) {
        return Completable.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("INSERT INTO public.\"Uploads\" (account_id,file) VALUES (?,?)")
                        .execute(Tuple.of(UUID.fromString(id), fileData))
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
                        })
                )
        );

    }

    @Override
    public Single<Set<String>> getUploads(String id) {
        return Single.create(emitter -> pool.getConnection()
                .onFailure(error -> {
                    System.out.println(error.getCause().getMessage());
                    emitter.onError(error);
                })
                .onSuccess(connection -> connection.preparedQuery("SELECT * FROM public.\"Uploads\" WHERE public.\"Uploads\".\"account_id\" = ?")
                        .execute(Tuple.of(UUID.fromString(id)))
                        .onFailure(error -> {
                            System.out.println(error);
                            emitter.onError(error);
                            connection.close();
                        })
                        .onSuccess(rows -> {
                            Set<String> files = new HashSet<>();
                            for(Row row : rows){
                                files.add(row.getString("file"));
                            }
                            emitter.onSuccess(files);
                            connection.close();
                        })
                )
        );
    }
}
