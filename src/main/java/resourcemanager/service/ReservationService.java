package resourcemanager.service;

import javafx.collections.ObservableList;
import resourcemanager.logic.ReservationLogic;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;
import resourcemanager.model.dto.GeneratedReservationDTO;
import resourcemanager.model.dto.ReservationDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.Consumer;

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

    public static void promptAI(String prompt, Consumer<GeneratedReservationDTO> onSuccess, Consumer<Exception> onError){
        ReservationLogic.promptAI(prompt, onSuccess, onError);
    }

    public static ArrayList<Reservation> filterByDate(ArrayList<Reservation> list, LocalDate start, LocalDate end){
        return ReservationLogic.filterByDate(list,start,end);
    }
    public static ArrayList<Resource> extractResources(ArrayList<Reservation> list) throws Exception {
        return ReservationLogic.extractResources(list);
    }
}
