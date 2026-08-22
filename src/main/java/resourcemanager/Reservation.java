package resourcemanager;

import java.time.LocalDate;

public class Reservation {
    private Resource resource;
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;

    public Reservation(){
        this.id="undefined";
    }

    public Reservation(Resource resource, String id, LocalDate startDate, LocalDate endDate){
        this.resource=resource;
        this.id=id;
        this.startDate=startDate;
        this.endDate=endDate;
    }
    public Resource getResource() { return resource; }
    public void setResource(Resource resource) { this.resource = resource; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
