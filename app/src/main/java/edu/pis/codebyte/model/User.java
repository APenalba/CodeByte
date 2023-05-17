package edu.pis.codebyte.model;


public class User {

    private String uId;
    private String username;
    private String uImageURL;
    private String email;
    private String provider;
    private UserProgress progress;

    public User(String uId, String username, String email, String uImageURL, String provider) {
        this.uId = uId;
        this.username = username;
        this.email = email;
        this.uImageURL = uImageURL;
        this.provider = provider;
        progress = new UserProgress(this.uId);
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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

    public void setProgress(UserProgress up) {
        this.progress = up;
    }
    public void addLanguageToProgress(String language) {
        progress.addLanguageToProgress(language);
    }
    public void addCourseToProgress(String course, String language) {
        progress.addCourseToProgress(course, language);
    }
    public void addLessonToProgress(String lessonName, String courseName, String language) {
        progress.addLessonToProgress(lessonName, courseName, language);
    }
    public UserProgress getProgress() {
        return progress;
    }
}
