package domain.model;

import java.util.HashSet;
import java.util.Set;

public class Account {
    private String username;
    private String password;
    private String uuid;

    private Set<Attraction> savedAttractions;

    public Account() {
        this.savedAttractions = new HashSet<>();
    }

    public Account(String username, String password, String uuid, Set<Attraction> savedAttractions) {
        this.username = username;
        this.password = password;
        this.uuid = uuid;
        this.savedAttractions = savedAttractions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Set<Attraction> getSavedAttractions() {
        return savedAttractions;
    }

    public void setSavedAttractions(Set<Attraction> savedAttractions) {
        this.savedAttractions = savedAttractions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", uuid='" + uuid + '\'' +
                ", savedAttractions=" + savedAttractions +
                '}';
    }
}
