package domain.application.command;

public class RemoveAttractionCommand {
    String accountId;
    String attractionName;
    String attractionLocation;

    public RemoveAttractionCommand() {
    }

    public RemoveAttractionCommand(String accountId, String attractionName, String attractionLocation) {
        this.accountId = accountId;
        this.attractionName = attractionName;
        this.attractionLocation = attractionLocation;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public String getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(String attractionLocation) {
        this.attractionLocation = attractionLocation;
    }
}
