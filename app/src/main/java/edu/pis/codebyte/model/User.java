package edu.pis.codebyte.model;


public class User {

    private String uId;
    private String username;
    private String uImageURL;
    private String email;

    public User(String uId, String username, String email) {
        this.uId = uId;
        this.username = username;
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getuImageURL() {
        return uImageURL;
    }

    public void setuImageURL(String uImageURL) {
        this.uImageURL = uImageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
