package resourcemanager;

import java.time.LocalDate;

public class Reservation {
    private Resource resource;
    private String id;
    private LocalDate startDate;
    private LocalDate endDate;

    public Reservation(Resource resource, String id, LocalDate startDate, LocalDate endDate){
        this.resource=resource;
        this.id=id;
        this.startDate=startDate;
        this.endDate=endDate;
    }
}
