package resourcemanager.structure;

import resourcemanager.User;

import java.util.ArrayList;

public class UserList {
    private ArrayList<User> users;

    public UserList(){
        users = new ArrayList<User>();
    }

    public void add(User r){
        users.add(r);
    }
}
