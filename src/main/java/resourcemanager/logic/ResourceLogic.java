package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.model.Resource;

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
}
