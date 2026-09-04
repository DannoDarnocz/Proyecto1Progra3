package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.data.SaveToXML;
import resourcemanager.data.DataPaths;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;

import javax.management.InstanceNotFoundException;
import java.security.InvalidParameterException;

import java.io.File;
import java.util.ArrayList;

public class ResourceLogic {
    private SaveToXML saveToXML=new SaveToXML();
    private LoadFromXML loadFromXML=new LoadFromXML();
    private CategoryLogic categoryLogic = new CategoryLogic();
    private PrintLogic printLogic = new PrintLogic();

    // buscar el recurso que corresponda con el ID
    public  Resource findResourceById(String id) throws Exception {
        ArrayList<Resource> resources = loadFromXML.loadResources();
        for(Resource currentResource : resources){
            if(currentResource .getId().equals(id)) return currentResource ;
        }
        return null; // no se encontro
    }

    public  Resource searchByDescriptionAndCategory(String description, String categoryId) throws Exception {
        ArrayList<Resource> allResources = loadFromXML.loadResources();
        description = description.toLowerCase();

        for (Resource r : allResources) {
            if (r.getDescription().toLowerCase().equals(description) && r.getCategoryId().equals(categoryId)) {
                return r;
            }
        }
        return null;
    }

    public ArrayList<Reservation> findReservationsForCategory(String id) throws Exception {
        ArrayList<Reservation> matchingReservations = new ArrayList<>();
        ArrayList<Reservation> allReservations = loadFromXML.loadReservations();

        // ver cuales reservas tienen asignado el recurso
        for(Reservation r:allReservations){
            ArrayList<String> resourcesForReservation = r.getResourceIdList();
            for(String resourceId : resourcesForReservation){
                // si al menos un recurso coincide con la categoria, agregar la reserva
                if(findResourceById(resourceId).getCategoryId().equals(id)) {
                    matchingReservations.add(r);
                 break;}
            }
        }

        if(matchingReservations.isEmpty()) return null;
        return matchingReservations;
    }

    public  ArrayList<Resource> getAllResources() throws Exception{
        return loadFromXML.loadResources();
    }

    public void printAllCategories() throws Exception {
        // obtener lista de todas las categorias
        ArrayList<Resource> allResources = getAllResources();

        // enviar a que la clase de impresion se encargue de generar el pdf
        File pdf = printLogic.generatePdf(allResources, Resource.class, "lista_de_recursos.pdf");
        printLogic.openPdf(pdf);
    }

    public  Resource addResource(String idCat, String description) throws Exception {
        if (idCat == null || idCat.isBlank() || categoryLogic.findCategoryById(idCat) == null) {
            throw new InvalidParameterException("Debe seleccionar una categoría válida");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidParameterException("La descripción no puede estar vacía");
        }
        //Lista de recursos disponibles
        ArrayList<Resource> allResources = getAllResources();

        //No se verifica igualdad debido a que puede haber varios recursos con el mismo nombre, pero la id nunca será igual al ser autogenerada
        int maxN = 0;
        for (Resource r : allResources) {
            String digits = r.getId().replaceAll("\\D+", "");
            if (!digits.isEmpty()) maxN = Math.max(maxN, Integer.parseInt(digits));
        }
        String newId = "res" + (maxN + 1);

        Resource nuevo = new Resource(newId, idCat, description.trim());
        allResources.add(nuevo);
        saveToXML.saveList(DataPaths.getResourcesFile(), "resources", "resource", allResources);
        return nuevo;
    }
    public  boolean updateResource(Resource updatedTarget) throws Exception {
        Resource currentTarget = findResourceById(updatedTarget.getId());
        if (currentTarget == null) {
            throw new InstanceNotFoundException("No se encontró un recurso con ese ID");
        }
        if (updatedTarget.getDescription() == null || updatedTarget.getDescription().isBlank()) {
            throw new InvalidParameterException("La descripción no puede estar vacía");
        }
        if (updatedTarget.getCategoryId() == null || categoryLogic.findCategoryById(updatedTarget.getCategoryId()) == null) {
            throw new InvalidParameterException("Debe seleccionar una categoría válida");
        }

        ArrayList<Resource> allResources = loadFromXML.loadResources();
        for (int i = 0; i < allResources.size(); i++) {
            if (allResources.get(i).getId().equals(updatedTarget.getId())) {
                allResources.set(i, updatedTarget);
                saveToXML.saveList(DataPaths.getResourcesFile(), "resources", "resource", allResources);
                return true;
            }
        }
        return false;
    }

    public boolean deleteResource(String id) throws Exception {
        Resource target = findResourceById(id);
        if (target == null) return false;

        if (!resourceIsOrphan(target)) {
            throw new RuntimeException("Existe al menos una reserva asociada con este recurso.");
        }

        ArrayList<Resource> allResources = loadFromXML.loadResources();
        allResources.remove(target);
        saveToXML.saveList(DataPaths.getResourcesFile(), "resources", "resource", allResources);
        return true;
    }

    public boolean resourceIsOrphan(Resource target) throws Exception {
        // obtener lista de reservas. si alguna tiene asociada el recurso
        // si tiene asociado un recurso entonces no se puede borrar
        ArrayList<Reservation> allReservations = loadFromXML.loadReservations();
        for (Reservation r : allReservations) {
            if (r.getResourceIdList() != null && r.getResourceIdList().contains(target.getId())) return false;
        }
        //No tiene reserva asociada
        return true;
    }

    public  ArrayList<Resource> findFreeResources() throws Exception {
        // cargar todos los recursos (la lista se modificará conforme se encuentren recursos ocupados)

        ArrayList<Resource> leftoverResources= loadFromXML.loadResources();
        ArrayList<User> allUsers = loadFromXML.loadUsers();

        // recorrer todos los usuarios e ir viendo cuáles estan ocupados
        for(User currentUser : allUsers){
            ArrayList<String> currentUserReservations = currentUser.getReservationIdList();
            // recorrer todas las id de reservaciones del usuario
            for(String currentReservationid : currentUserReservations) {
                // buscar reserva por id
                ReservationLogic reservationLogic = new ReservationLogic(); // aca se necesita saber sobre reservas pero no es una dependencia

                Reservation currentReservation = reservationLogic.findReservationById(currentReservationid);
                if(currentReservation!=null) {
                    ArrayList<String> currentResourcesIDs = currentReservation.getResourceIdList();
                    // recorrer todos los recursos de la reservación
                    for (String currentResourceID : currentResourcesIDs) {
                        Resource currentResource = findResourceById(currentResourceID);
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

    public  Resource findFirstResourceFree(Category category) throws Exception{
        ArrayList<Resource> freeResources = findFreeResources();

        // buscar cual recurso de los libres tiene un ID de categoria que se busca
        for(Resource r : freeResources){
            if(r.getCategoryId().equals(category.getId())) return r;
        }
        return null;
    }
}
