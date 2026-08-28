package resourcemanager.model.dto;

import resourcemanager.model.Category;
import resourcemanager.model.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReservationDTO {
    private String id;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ArrayList<Category> categories;

    public ReservationDTO() {
    }

    public ReservationDTO(String id, String description, LocalDateTime startDate, LocalDateTime endDate, ArrayList<Category> categories) {
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.categories = categories;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description){ this.description=description;}

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
