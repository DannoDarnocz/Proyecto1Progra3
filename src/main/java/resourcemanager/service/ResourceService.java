package resourcemanager.service;

import resourcemanager.data.LoadFromXML;
import resourcemanager.logic.CategoryLogic;
import resourcemanager.logic.ResourceLogic;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;

import java.util.ArrayList;

public class ResourceService {
    public static ArrayList<Resource> getAllResources() throws Exception{
        return ResourceLogic.getAllResources();
    }

    public static Resource getResourceById(String id) throws Exception{
        return ResourceLogic.findResourceById(id);
    }
    public static void printAllCategories() throws Exception {
        ResourceLogic.printAllCategories();
    }
    public static Resource searchByDescriptionAndCategory(String desc, String categoryId) throws Exception {
        return ResourceLogic.searchByDescriptionAndCategory(desc, categoryId);
    }
    public static Resource addResource(String categoryId, String description) throws Exception {
        return ResourceLogic.addResource(categoryId, description);
    }

    public static boolean updateResource(Resource r) throws Exception {
        return ResourceLogic.updateResource(r);
    }

    public static boolean deleteResource(String id) throws Exception {
        return ResourceLogic.deleteResource(id);
    }
}
