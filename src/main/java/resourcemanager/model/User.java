package resourcemanager.model;


import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private String password;
    private String phoneNumber;
    private Boolean isAdmin;
    private ArrayList<Reservation> reservationList;

    public User(){
        id="undefined";
        name="undefined";
        password="123";
        phoneNumber="undefined";
        isAdmin=false;
        reservationList = new ArrayList<Reservation>();
    }

    public User(String id,String name,String password, Boolean isAdmin){
        this.id=id;
        this.name=name;
        this.password=password;
        this.isAdmin=isAdmin;
        reservationList=new ArrayList<Reservation>();
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

    public void addReservation(Reservation r) throws InstanceAlreadyExistsException{
        if(reservationList.contains(r)){
            throw new InstanceAlreadyExistsException();
        }
        reservationList.add(r); // automaticamente revisa si existe, sino no hace nada
    }
    public void removeReservation(Reservation r){
        reservationList.remove(r); // automaticamente revisa si existe, sino no hace nada
    }

    public ArrayList<Reservation> getReservationList() { return reservationList; }
    public void setReservationList(ArrayList<Reservation> reservationList) { this.reservationList = reservationList; }
}
