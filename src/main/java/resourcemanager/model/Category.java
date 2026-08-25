package resourcemanager.model;

public class Category {
    private String id;
    private String description;

    public Category(String id, String description){
        this.id=id;
        this.description=description;
    }

    public Category(){
        this.id="undefined";
        this.description="undefined";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
