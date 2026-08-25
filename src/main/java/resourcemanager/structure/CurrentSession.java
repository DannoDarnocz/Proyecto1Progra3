package resourcemanager.structure;

import resourcemanager.model.User;

public class CurrentSession {
    private static CurrentSession instance;
    private User loggedUser;

    // singleton
    private CurrentSession() {}

    public static CurrentSession getInstance() {
        if (instance == null) {
            instance = new CurrentSession();
        }
        return instance;
    }

    public User getLoggedUser() { return loggedUser; }
    public void setLoggedUser(User user) { this.loggedUser = user; }

    public boolean isLoggedIn() { return loggedUser != null; }

    public void logout() { loggedUser = null; }
}
