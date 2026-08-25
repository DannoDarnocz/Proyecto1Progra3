package resourcemanager.structure;
import resourcemanager.filehandler.*;

public class GlobalLists {
    private static GlobalLists instance;
    public static UserList userList = new UserList();
    public static ResourceList resourceList = new ResourceList();
    public static ReservationList reservationList = new ReservationList();
    public static CategoryList categoryList = new CategoryList();

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
        userList = LoadXML.load("/resourcemanager/data/users.xml", UserList.class);
    }

    public UserList getUsers() { return userList; }
    public ResourceList getResources() { return resourceList; }
    public ReservationList getReservations() { return reservationList; }
    public CategoryList getCategories() { return categoryList; }
}
