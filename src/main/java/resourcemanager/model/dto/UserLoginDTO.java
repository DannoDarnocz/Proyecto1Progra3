package resourcemanager.model.dto;

public class UserLoginDTO {
    private String user;
    private String password;

    public UserLoginDTO() {
    }

    public UserLoginDTO(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
