package resourcemanager.model.dto;

import resourcemanager.model.Category;
import resourcemanager.model.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReservationDTO {
    // DTO no tiene la lista de Resources ni ID porque eso se construye después
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ReservationDTO() {
    }

    public ReservationDTO(String description, LocalDateTime startDate, LocalDateTime endDate) {

        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public String getDescription() { return description; }
    public void setDescription(String description){ this.description=description;}

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
}
