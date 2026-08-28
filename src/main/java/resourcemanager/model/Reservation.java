package resourcemanager.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Reservation {
    // esto es para que al tomar el XML sepa que es un array. El tag de resources contiene multiples resource (o uno)
    @JacksonXmlElementWrapper(useWrapping = true, localName = "resources")
    @JacksonXmlProperty(localName = "resource")
    private ArrayList<Resource> resources;
    private String id;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;

    public Reservation(){
        this.resources=new ArrayList<Resource>();
        description = "undefined";
        this.id="undefined";
        startDate = null;
        endDate = null;
    }

    public Reservation(String id, String description, LocalDateTime startDate, LocalDateTime endDate){
        this.resources=new ArrayList<Resource>();
        this.isActive=true; // siempre empeiza activa porque no tendria sentido que empiece inactiva
        this.id=id;
        this.description = description;
        this.startDate=startDate;
        this.endDate=endDate;
    }
    public ArrayList<Resource> getResources() { return resources; }
    public void setResource(ArrayList<Resource> resource) { this.resources = resource; }

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

    // TODO: DTO o no?
    public void addResource(Resource r){
        this.resources.add(r);
    }
}
