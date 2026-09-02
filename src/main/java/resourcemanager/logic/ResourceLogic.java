package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.data.SaveToXML;
import resourcemanager.data.DataPaths;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;
import resourcemanager.model.Reservation;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.security.InvalidParameterException;

import java.io.File;
import java.util.ArrayList;

public class ResourceLogic {
    // buscar el recurso que corresponda con el ID
    public static Resource findResourceById(String id) throws Exception {
        ArrayList<Resource> resources = LoadFromXML.loadResources();
        for(Resource currentResource : resources){
            if(currentResource .getId().equals(id)) return currentResource ;
        }
        return null; // no se encontro
    }

    public static Resource searchByDescriptionAndCategory(String description, String categoryId) throws Exception {
        ArrayList<Resource> allResources = LoadFromXML.loadResources();
        description = description.toLowerCase();

        for (Resource r : allResources) {
            if (r.getDescription().toLowerCase().equals(description) && r.getCategoryId().equals(categoryId)) {
                return r;
            }
        }
        return null;
    }

    public static ArrayList<Resource> getAllResources() throws Exception{
        return LoadFromXML.loadResources();
    }

    public static void printAllCategories() throws Exception {
        // obtener lista de todas las categorias
        ArrayList<Resource> allResources = getAllResources();

        // enviar a que la clase de impresion se encargue de generar el pdf
        File pdf = PrintLogic.generatePdf(allResources, Resource.class, "lista_de_recursos.pdf");
        PrintLogic.openPdf(pdf);
    }

    public static Resource addResource(String idCat, String description) throws Exception {
        if (idCat == null || idCat.isBlank() || CategoryLogic.findCategoryById(idCat) == null) {
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
        SaveToXML.saveList(DataPaths.getResourcesFile(), "resources", "resource", allResources);
        return nuevo;
    }
    public static boolean updateResource(Resource updatedTarget) throws Exception {
        Resource currentTarget = findResourceById(updatedTarget.getId());
        if (currentTarget == null) {
            throw new InstanceNotFoundException("No se encontró un recurso con ese ID");
        }
        if (updatedTarget.getDescription() == null || updatedTarget.getDescription().isBlank()) {
            throw new InvalidParameterException("La descripción no puede estar vacía");
        }
        if (updatedTarget.getCategoryId() == null || CategoryLogic.findCategoryById(updatedTarget.getCategoryId()) == null) {
            throw new InvalidParameterException("Debe seleccionar una categoría válida");
        }

        ArrayList<Resource> allResources = LoadFromXML.loadResources();
        for (int i = 0; i < allResources.size(); i++) {
            if (allResources.get(i).getId().equals(updatedTarget.getId())) {
                allResources.set(i, updatedTarget);
                SaveToXML.saveList(DataPaths.getResourcesFile(), "resources", "resource", allResources);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteResource(String id) throws Exception {
        Resource target = findResourceById(id);
        if (target == null) return false;

        if (!resourceIsOrphan(target)) {
            throw new RuntimeException("Existe al menos una reserva asociada con este recurso.");
        }

        ArrayList<Resource> allResources = LoadFromXML.loadResources();
        allResources.remove(target);
        SaveToXML.saveList(DataPaths.getResourcesFile(), "resources", "resource", allResources);
        return true;
    }

    public static boolean resourceIsOrphan(Resource target) throws Exception {
        // obtener lista de reservas. si alguna tiene asociada el recurso
        // si tiene asociado un recurso entonces no se puede borrar
        ArrayList<Reservation> allReservations = LoadFromXML.loadReservations();
        for (Reservation r : allReservations) {
            if (r.getResourceIdList() != null && r.getResourceIdList().contains(target.getId())) return false;
        }
        //No tiene reserva asociada
        return true;
    }
}
