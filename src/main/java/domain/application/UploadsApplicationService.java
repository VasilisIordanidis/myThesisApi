package domain.application;

import domain.application.command.AddFileCommand;
import domain.application.query.GetUploadsByIdQuery;
import domain.model.AttractionRepository;
import domain.model.UploadRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.io.File;
import java.util.Set;

public class UploadsApplicationService {
    private final UploadRepository uploadRepository;

    public UploadsApplicationService(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    public Completable execute(AddFileCommand command){
       return this.uploadRepository.addUpload(command.getId(),command.getFile());
    }

    public Single<Set<File>> execute(GetUploadsByIdQuery query){
        return this.uploadRepository.getUploads(query.getId());
    }

}
