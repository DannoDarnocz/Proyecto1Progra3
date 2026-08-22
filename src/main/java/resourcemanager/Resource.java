package resourcemanager;

public class Resource {
    private String id;
    private Category category;
    private String description;

    public Resource(String id, Category category, String description){
        this.id=id;
        this.category = category;
        this.description=description;
    }

    public Resource(){
        this.id="undefined";
        this.description="undefined";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
