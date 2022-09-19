package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Set;

public interface UploadRepository {
    Completable addUpload(String id, byte[] file);
    Single<Set<File>> getUploads(String id);
}
