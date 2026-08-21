package resourcemanager.structure;

import resourcemanager.Reservation;

import java.util.ArrayList;

public class ReservationList extends ObjectList {
    private ArrayList<Reservation> reservations;

    public ReservationList(){
        reservations = new ArrayList<Reservation>();
    }

    public void add(Reservation r){
        reservations.add(r);
    }
}
