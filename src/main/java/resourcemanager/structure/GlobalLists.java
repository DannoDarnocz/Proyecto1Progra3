package resourcemanager.structure;

import resourcemanager.Category;
import resourcemanager.Reservation;
import resourcemanager.Resource;
import resourcemanager.User;
import resourcemanager.filehandler.*;

import java.io.File;
import java.util.List;

public class GlobalLists {
    private static GlobalLists instance;
    private List<User> userList;
    private List<Resource> resourceList;
    private List<Reservation> reservationList;
    private List<Category> categoryList;

    private GlobalLists() {
        // singleotn
    }

    public static GlobalLists getInstance() {
        if (instance == null) {
            instance = new GlobalLists();
        }
        return instance;
    }

    public void loadAll() throws Exception {
        categoryList = LoadXML.loadList(new File("categories.xml"), Category.class);
        resourceList = LoadXML.loadList(new File("resources.xml"), Resource.class);
        reservationList = LoadXML.loadList(new File("reservations.xml"), Reservation.class);
        userList = LoadXML.loadList(new File("users.xml"), User.class);
    }

    public User searchUser(String targetId){
        for(User user : userList) {
            if(user.getId().equals(targetId)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getUsers() { return userList; }
    public List<Resource> getResources() { return resourceList; }
    public List<Reservation> getReservations() { return reservationList; }
    public List<Category> getCategories() { return categoryList; }
}
