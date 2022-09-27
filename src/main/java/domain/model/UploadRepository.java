package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.io.*;
import java.util.Set;

public interface UploadRepository {
    Completable addUpload(String id, String fileData);
    Single<Set<String>> getUploads(String id);
}
