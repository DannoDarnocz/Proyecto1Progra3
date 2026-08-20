package resourcemanager;

import resourcemanager.structure.ReservationList;

public class User {
    private String id;
    private String name;
    private String password;
    private String phoneNumber;
    private Boolean isAdmin;
    private ReservationList reservationList;

    public User(String id,String name,String password, Boolean isAdmin){
        this.id=id;
        this.name=name;
        this.password=password;
        this.isAdmin=isAdmin;
        reservationList=new ReservationList();
    }
}
