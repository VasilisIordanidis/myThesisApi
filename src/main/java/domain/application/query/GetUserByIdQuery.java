package domain.application.query;

public class GetUserByIdQuery {
    String id;

    public GetUserByIdQuery() {
    }

    public GetUserByIdQuery(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
