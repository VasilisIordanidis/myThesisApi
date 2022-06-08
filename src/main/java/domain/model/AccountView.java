package domain.model;

import java.util.HashSet;
import java.util.Set;

public class AccountView {
    private String username;
    //private String id;
    private Set<Attraction> attractionSet;

    public AccountView() {
        this.attractionSet = new HashSet<>();
    }

    public AccountView(String username, Set<Attraction> attractionSet) {
        this.username = username;
        this.attractionSet = attractionSet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Attraction> getAttractionSet() {
        return attractionSet;
    }

    public void setAttractionSet(Set<Attraction> attractionSet) {
        this.attractionSet = attractionSet;
    }

    @Override
    public String toString() {
        return "AccountView{" +
                "username='" + username + '\'' +
                ", attractionSet=" + attractionSet +
                '}';
    }
}
