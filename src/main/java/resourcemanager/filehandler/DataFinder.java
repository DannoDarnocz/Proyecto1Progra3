package resourcemanager.filehandler;

import resourcemanager.logic.ReservationLogic;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataFinder  {
    public static ArrayList<Resource> findFreeResources() throws Exception {
        // cargar todos los recursos (la lista se modificará conforme se encuentren recursos ocupados)

        ArrayList<Resource> leftoverResources= LoadXML.loadList(FileLocations.RESOURCES_PATH, Resource.class);
        ArrayList<User> allUsers = LoadXML.loadList(FileLocations.USERS_PATH, User.class);

        // recorrer todos los usuarios e ir viendo cuáles estan ocupados
        for(User currentUser : allUsers){
            ArrayList<String> currentUserReservations = currentUser.getReservationIdList();
            // recorrer todas las id de reservaciones del usuario
            for(String currentReservationid : currentUserReservations){
                // buscar reserva por id

                Reservation currentReservation = ReservationLogic.findReservationById(currentReservationid);
                if(currentReservation!=null && currentReservation.isActive()) {
                    // solo cuenta si esta activa
                    ArrayList<Resource> currentResources = currentReservation.getResources();
                    // recorrer todos los recursos de la reservación
                    for (Resource currentResource : currentResources) {
                        if (leftoverResources.contains(currentResource)) {
                            leftoverResources.remove(currentResource);
                        } else {
                            // todo: algo anda raro porque todas las resources deberian estar
                        }
                    }
                }
            }
        }

        return leftoverResources;
    }

    public static Resource findFirstResourceFree(Category category) throws Exception{
        ArrayList<Resource> freeResources = findFreeResources();

        for(Resource r : freeResources){
            if(r.getCategory().equals(category)) return r;
        }
        return null;
    }

    public static ArrayList<Category> findFreeCategories() throws Exception {
        // encontrar primero todos los recursos disponibles, luego recorrerlos añadiendo categorías libres para
        // estar seguros de su disponibilidad de al menos 1
        ArrayList<Resource> freeResouces = findFreeResources();

        // set automaticamente se asegura de no duplicar categorías
        Set<Category> categoriesWithFreeResources = new LinkedHashSet<>();

        for(Resource currentResource : freeResouces){
            categoriesWithFreeResources.add(currentResource.getCategory());
        }

        // convertir a arraylist creandola y copiando lo que tiene
        return new ArrayList<>(categoriesWithFreeResources);
    }
}
