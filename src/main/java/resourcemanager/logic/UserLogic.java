package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.service.ReservationService;

import java.io.File;
import java.util.ArrayList;

public class UserLogic {
    // buscar el usuario que corresponda con el ID
    public static User findUserById(String id) throws Exception {
        ArrayList<User> users = LoadFromXML.loadUsers();
        for(User currentUser : users){
            if(currentUser.getId().equals(id)) return currentUser;
        }
        return null; // no se encontro
    }
    public static ArrayList<Reservation> findReservationsForUser(User user) {
        ArrayList<String> reservationIds = user.getReservationIdList();
        ArrayList<Reservation> reservations = new ArrayList<>();

        for(String currentID : reservationIds){
            // buscar reserva si existe por id (pasandolo por el layer de service por si acaso algo cambia en la logica)
            try{
                Reservation currentReservation = ReservationService.findReservationById(currentID);
                if(currentReservation!=null) reservations.add(currentReservation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (reservations.isEmpty()) return null; // esta vacia, no se puede hacer nada
        return reservations;
    }

    public static void printUserReservations(User user) throws Exception {
        // obtener lista de reservas del usuario
        ArrayList<Reservation> reservations = findReservationsForUser(user);

        // generar archivo pdf y abrirselo al usuario
        File pdf = PrintLogic.generatePdf(reservations, Reservation.class, "reservas_usuario.pdf");
        PrintLogic.openPdf(pdf);
    }
}
