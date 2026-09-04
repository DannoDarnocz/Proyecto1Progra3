package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.service.ReservationService;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.io.File;
import java.util.ArrayList;

public class UserLogic {
    private LoadFromXML loadFromXML = new LoadFromXML();
    private PrintLogic printLogic = new PrintLogic();
    private ReservationLogic reservationLogic = new ReservationLogic();

    // buscar el usuario que corresponda con el ID
    public User findUserById(String id) throws Exception {
        ArrayList<User> users = loadFromXML.loadUsers();
        for(User currentUser : users){
            if(currentUser.getId().equals(id)) return currentUser;
        }
        return null; // no se encontro
    }
    public ArrayList<Reservation> findReservationsForUser(User user) {
        ArrayList<String> reservationIds = user.getReservationIdList();
        ArrayList<Reservation> reservations = new ArrayList<>();

        for(String currentID : reservationIds){
            // buscar reserva si existe por id (pasandolo por el layer de service por si acaso algo cambia en la logica)
            try{
                Reservation currentReservation = reservationLogic.findReservationById(currentID);
                if(currentReservation!=null) reservations.add(currentReservation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (reservations.isEmpty()) return null; // esta vacia, no se puede hacer nada
        return reservations;
    }

    public void printUserReservations(User user) throws Exception {
        // obtener lista de reservas del usuario
        ArrayList<Reservation> reservations = findReservationsForUser(user);

        // generar archivo pdf y abrirselo al usuario
        File pdf = printLogic.generatePdf(reservations, Reservation.class, "reservas_usuario.pdf");
        printLogic.openPdf(pdf);
    }

    public User findUserForReservation(Reservation r) throws Exception {
        ArrayList<User> users = loadFromXML.loadUsers();
        for(User u : users){
            if(u.getReservationIdList().contains(r.getId())) return u;
        }

        return null;
    }

    // se maneja la lista por fuera porque User es un DTO, no puede tener métodos específicos
    public void addReservation(Reservation r, User u) throws InstanceAlreadyExistsException {
        ArrayList<String> reservationsForUser = u.getReservationIdList();

        if(reservationsForUser.contains(r.getId())){
            throw new InstanceAlreadyExistsException("Reserva a agregar ya esta asignada a usuario");
        }
        reservationsForUser.add(r.getId()); // automaticamente revisa si existe, sino no hace nada
    }

    public void removeReservation(Reservation r, User u) throws InstanceNotFoundException {
        ArrayList<String> reservationsForUser = u.getReservationIdList();

        if(reservationsForUser.contains(r.getId())){
            reservationsForUser.remove(r.getId());
        } else {
            throw new InstanceNotFoundException("Reserva a borrar no existe");
        }
    }
}
