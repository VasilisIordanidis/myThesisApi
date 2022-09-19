package domain.application.command;

import java.io.File;

public class AddFileCommand {
    private String id;
    private byte[] file;

    public AddFileCommand(String id, byte[] file) {
        this.id = id;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public byte[] getFile() {
        return file;
    }
}
