package resourcemanager.service;

import resourcemanager.logic.ResourceLogic;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;

import java.util.ArrayList;

public class ResourceService {
    private ResourceLogic resourceLogic = new ResourceLogic();

    public ArrayList<Resource> getAllResources() throws Exception{
        return resourceLogic.getAllResources();
    }

    public Resource getResourceById(String id) throws Exception{
        return resourceLogic.findResourceById(id);
    }
    public void printAllCategories() throws Exception {
        resourceLogic.printAllCategories();
    }
    public Resource searchByDescriptionAndCategory(String desc, String categoryId) throws Exception {
        return resourceLogic.searchByDescriptionAndCategory(desc, categoryId);
    }
    public  Resource addResource(String categoryId, String description) throws Exception {
        return resourceLogic.addResource(categoryId, description);
    }

    public  ArrayList<Reservation> findReservationsForCategory(String resourceId) throws Exception {
        return resourceLogic.findReservationsForCategory(resourceId);
    }

    public boolean updateResource(Resource r) throws Exception {
        return resourceLogic.updateResource(r);
    }

    public boolean deleteResource(String id) throws Exception {
        return resourceLogic.deleteResource(id);
    }
}
