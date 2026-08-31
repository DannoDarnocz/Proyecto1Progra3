package resourcemanager.service;

import resourcemanager.data.LoadFromXML;
import resourcemanager.logic.ResourceLogic;
import resourcemanager.model.Resource;

import java.util.ArrayList;

public class ResourceService {
    public static ArrayList<Resource> getAllResources() throws Exception{
        return ResourceLogic.getAllResources();
    }

    public static Resource getResourceById(String id) throws Exception{
        return ResourceLogic.findResourceById(id);
    }
}
