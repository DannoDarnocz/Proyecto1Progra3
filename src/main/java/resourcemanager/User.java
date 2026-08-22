package resourcemanager;

import resourcemanager.structure.ReservationList;

public class User {
    private String id;
    private String name;
    private String password;
    private String phoneNumber;
    private Boolean isAdmin;
    private ReservationList reservationList;

    public User(){
        id="undefined";
        name="undefined";
        password="123";
        phoneNumber="undefined";
        isAdmin=false;
        reservationList = new ReservationList();
    }

    public User(String id,String name,String password, Boolean isAdmin){
        this.id=id;
        this.name=name;
        this.password=password;
        this.isAdmin=isAdmin;
        reservationList=new ReservationList();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public ReservationList getReservationList() { return reservationList; }
    public void setReservationList(ReservationList reservationList) { this.reservationList = reservationList; }
}
