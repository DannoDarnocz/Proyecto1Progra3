package resourcemanager.data;

import resourcemanager.logic.CategoryLogic;
import resourcemanager.logic.ReservationLogic;
import resourcemanager.logic.ResourceLogic;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;

import javax.management.InstanceNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataHandler {
    public static ArrayList<Resource> findFreeResources() throws Exception {
        // cargar todos los recursos (la lista se modificará conforme se encuentren recursos ocupados)

        ArrayList<Resource> leftoverResources= LoadFromXML.loadResources();
        ArrayList<User> allUsers = LoadFromXML.loadUsers();

        // recorrer todos los usuarios e ir viendo cuáles estan ocupados
        for(User currentUser : allUsers){
            ArrayList<String> currentUserReservations = currentUser.getReservationIdList();
            // recorrer todas las id de reservaciones del usuario
            for(String currentReservationid : currentUserReservations){
                // buscar reserva por id
                Reservation currentReservation = ReservationLogic.findReservationById(currentReservationid);
                if(currentReservation!=null) {
                    ArrayList<String> currentResourcesIDs = currentReservation.getResourceIdList();
                    // recorrer todos los recursos de la reservación
                    for (String currentResourceID : currentResourcesIDs) {
                        Resource currentResource = ResourceLogic.findResourceById(currentResourceID);
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

        // buscar cual recurso de los libres tiene un ID de categoria que se busca
        for(Resource r : freeResources){
            if(r.getCategoryId().equals(category.getId())) return r;
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
            System.out.println("current resource category id " + currentResource.getCategoryId());
            // obtener id de categoria actual y buscar la instancia
            String categoryId = currentResource.getCategoryId();

            Category currentCategory = CategoryLogic.findCategoryById(categoryId);

            // si no se encuentra hay algo raro
            if(currentCategory==null) throw new InstanceNotFoundException("La categoria con ese ID no existe");

            categoriesWithFreeResources.add(currentCategory);
        }

        // convertir a arraylist creandola y copiando lo que tiene
        return new ArrayList<>(categoriesWithFreeResources);
    }
}
