package resourcemanager.service;

import resourcemanager.logic.UserLogic;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.structure.CurrentSession;

import java.util.ArrayList;

public class UserService {
    private UserLogic userLogic = new UserLogic();

    public User getLoggedUser(){
        CurrentSession session = CurrentSession.getInstance();
        return session.getLoggedUser();
    }

    public User findUserById(String id) throws Exception{
        return userLogic.findUserById(id);
    }

    public ArrayList<Reservation> findReservationsForUser(User user){
        return userLogic.findReservationsForUser(user);
    }

    public void printUserReservations(User user) throws Exception {
        userLogic.printUserReservations(user);
    }

    public User findUserForReservation(Reservation r) throws Exception {
        return userLogic.findUserForReservation(r);
    }
}
