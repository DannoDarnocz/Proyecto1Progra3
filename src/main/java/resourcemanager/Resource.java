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
}
