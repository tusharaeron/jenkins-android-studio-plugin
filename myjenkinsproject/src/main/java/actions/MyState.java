package actions;

public class MyState{
    private String jenkinsCrumb;
    private String username;
    private String password;
    private String authorizationToken;

    public String getJenkinsCrumb() {
        return jenkinsCrumb;
    }

    public void setJenkinsCrumb(String jenkinsCrumb) {
        this.jenkinsCrumb = jenkinsCrumb;
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

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public MyState(){}

}