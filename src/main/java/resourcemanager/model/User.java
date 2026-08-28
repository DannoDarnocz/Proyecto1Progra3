package resourcemanager.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private String password;
    private String phoneNumber;
    private Boolean isAdmin;
    private ArrayList<String> reservationIdList; // guardar IDs de las reservas, no las reservas como tal para facilitar XML

    // dejarle saber al lector XML cómo manejar estas etiquetas
    @JacksonXmlElementWrapper(useWrapping = true, localName = "reservationList")
    @JacksonXmlProperty(localName = "reservationId")
    private ArrayList<String> reservationIds;

    public User(){
        id="undefined";
        name="undefined";
        password="123";
        phoneNumber="undefined";
        isAdmin=false;
        reservationIdList = new ArrayList<String>();
    }

    public User(String id,String name,String password, Boolean isAdmin){
        this.id=id;
        this.name=name;
        this.password=password;
        this.isAdmin=isAdmin;
        reservationIdList = new ArrayList<String>();
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

    public ArrayList<String> getReservationIdList() { return reservationIdList; }
    public void setReservationList(ArrayList<String> reservationList) { this.reservationIdList = reservationList; }

    // TODO: DTO o no?
    // se maneja la lista por fuera porque User es un DTO, no puede tener métodos específicos
    public void addReservation(Reservation r) throws InstanceAlreadyExistsException {
        if(reservationIdList.contains(r.getId())){
            throw new InstanceAlreadyExistsException("Reserva a agregar ya esta asignada a usuario");
        }
        reservationIdList.add(r.getId()); // automaticamente revisa si existe, sino no hace nada
    }

    public void removeReservation(Reservation r) throws InstanceNotFoundException{
        // solo la quita del usuario, NO la elimina del archivo xml de resources ni lo pone disactivo porque eso no le corresponde
        if(reservationIdList.contains(r.getId())){
            reservationIdList.remove(r.getId());
        }
        throw new InstanceNotFoundException("Reserva a borrar no existe");
    }
}
