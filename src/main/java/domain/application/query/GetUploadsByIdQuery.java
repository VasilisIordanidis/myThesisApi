package domain.application.query;

public class GetUploadsByIdQuery {
    private String id;

    public GetUploadsByIdQuery(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
