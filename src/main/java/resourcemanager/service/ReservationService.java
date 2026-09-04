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
    private ReservationLogic reservationLogic = new ReservationLogic();

    // servicio direcciona al controlador a donde necesita (recepcionista)
    // deja pasar la excepción con "throws" porque eso le vale
    public Reservation createReservationForUser(ReservationDTO dto, ObservableList<Category> selectedItems, User user) throws Exception{
        return reservationLogic.createReservationForUser(dto,selectedItems, user);
    }

    public boolean deleteReservation(String reservationId, User user) throws Exception {
        return reservationLogic.deleteReservation(reservationId, user);
    }

    public Reservation findReservationById(String id) throws Exception {
        return reservationLogic.findReservationById(id);
    }

    public void promptAI(String prompt, Consumer<GeneratedReservationDTO> onSuccess, Consumer<Exception> onError){
        reservationLogic.promptAI(prompt, onSuccess, onError);
    }

    public ArrayList<Reservation> filterByDate(ArrayList<Reservation> list, LocalDate start, LocalDate end){
        return reservationLogic.filterByDate(list,start,end);
    }
    public ArrayList<Resource> extractResources(ArrayList<Reservation> list) throws Exception {
        return reservationLogic.extractResources(list);
    }
}
