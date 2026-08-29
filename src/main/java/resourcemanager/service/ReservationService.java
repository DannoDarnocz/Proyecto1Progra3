package resourcemanager.service;

import javafx.collections.ObservableList;
import resourcemanager.logic.ReservationLogic;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.model.dto.GeneratedReservationDTO;
import resourcemanager.model.dto.ReservationDTO;

public class ReservationService {
    // servicio direcciona al controlador a donde necesita (recepcionista)
    // deja pasar la excepción con "throws" porque eso le vale
    public static Reservation createReservationForUser(ReservationDTO dto, ObservableList<Category> selectedItems, User user) throws Exception{
        return ReservationLogic.createReservationForUser(dto,selectedItems, user);
    }

    public static boolean deleteReservation(String reservationId, User user) throws Exception {
        return ReservationLogic.deleteReservation(reservationId, user);
    }

    public static Reservation findReservationById(String id) throws Exception {
        return ReservationLogic.findReservationById(id);
    }

    public static GeneratedReservationDTO promptAI(String prompt) throws Exception{
        return ReservationLogic.promptAI(prompt);
    }
}
