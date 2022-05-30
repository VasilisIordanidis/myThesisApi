package domain.application.query;

public class GetSavedAttractionsQuery {
    String accountId;

    public GetSavedAttractionsQuery() {
    }

    public GetSavedAttractionsQuery(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
