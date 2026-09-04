package resourcemanager.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import resourcemanager.logic.ResourceLogic;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reservation {
    // dejarle saber al lector XML cómo manejar estas etiquetas
    @JacksonXmlElementWrapper(useWrapping = true, localName = "resourceIdList")
    @JacksonXmlProperty(localName = "resourceId")
    private ArrayList<String> resourceIdList;

    private String id;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;

    public Reservation(){
        this.resourceIdList =new ArrayList<String>();
        description = "undefined";
        this.id="undefined";
        startDate = null;
        endDate = null;
    }

    public Reservation(String id, String description, LocalDateTime startDate, LocalDateTime endDate){
        this.resourceIdList =new ArrayList<String>();
        this.isActive=true; // siempre empeiza activa porque no tendria sentido que empiece inactiva
        this.id=id;
        this.description = description;
        this.startDate=startDate;
        this.endDate=endDate;
    }
    public ArrayList<String> getResourceIdList() { return resourceIdList; }
    public void setResourceIdList(ArrayList<String> resource) { this.resourceIdList = resource; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description){ this.description=description;}

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString(){
        return "id: " + id+", description: "+ description+", startDate: "+startDate+", endDate: " +endDate;
    }

}
