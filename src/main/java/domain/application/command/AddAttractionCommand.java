package domain.application.command;

public class AddAttractionCommand {
    String accountId;
    String attractionName;
    String location;
    double rating;
    int totalReviews;
    String imgUrl;//na dw pws kanw store img

    public AddAttractionCommand() {
    }

    public AddAttractionCommand(String accountId, String attractionName, String location, double rating, int totalReviews, String imgUrl) {
        this.accountId = accountId;
        this.attractionName = attractionName;
        this.location = location;
        this.rating = rating;
        this.totalReviews = totalReviews;
        this.imgUrl = imgUrl;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
