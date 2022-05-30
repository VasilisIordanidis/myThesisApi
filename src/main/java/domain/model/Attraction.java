package domain.model;

public class Attraction {
    private String name;
    private int totalReviews;
    private double rating;
    private String image;
    private String address;
    public Attraction() {
    }

    public Attraction(String name, int totalReviews, double rating, String image, String address) {
        this.name = name;
        this.totalReviews = totalReviews;
        this.rating = rating;
        this.image = image;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
