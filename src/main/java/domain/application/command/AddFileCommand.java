package domain.application.command;

import java.io.File;

public class AddFileCommand {
    private String id;
    private String fileData;

    public AddFileCommand(String id, String fileData) {
        this.id = id;
        this.fileData = fileData;
    }

    public String getId() {
        return id;
    }

    public String getFile() {
        return fileData;
    }
}
