package domain.application.command;

public class DeleteUserCommand {
    String id;

    public DeleteUserCommand() {
    }

    public DeleteUserCommand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
