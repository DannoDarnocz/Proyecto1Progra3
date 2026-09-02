package resourcemanager.service;

import resourcemanager.logic.UserLogic;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.structure.CurrentSession;

import java.util.ArrayList;

public class UserService {
    public static User getLoggedUser(){
        CurrentSession session = CurrentSession.getInstance();
        return session.getLoggedUser();
    }

    public static User findUserById(String id) throws Exception{
        return UserLogic.findUserById(id);
    }

    public static ArrayList<Reservation> findReservationsForUser(User user){
        return UserLogic.findReservationsForUser(user);
    }

    public static void printUserReservations(User user) throws Exception {
        UserLogic.printUserReservations(user);
    }

    public static User findUserForReservation(Reservation r) throws Exception {
        return UserLogic.findUserForReservation(r);
    }
}
