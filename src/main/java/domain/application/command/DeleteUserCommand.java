package domain.application.command;

public class DeleteUserCommand {
    String email;

    public DeleteUserCommand() {
    }

    public DeleteUserCommand(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
