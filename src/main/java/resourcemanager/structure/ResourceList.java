package resourcemanager.structure;

import resourcemanager.Resource;

import java.util.ArrayList;

public class ResourceList {
    private ArrayList<Resource> resources;

    public ResourceList(){
        resources= new ArrayList<Resource>();
    }

    public void add(Resource r){
        resources.add(r);
    }
}
