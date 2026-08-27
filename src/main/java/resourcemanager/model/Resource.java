package resourcemanager.model;


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

    @Override
    // esto es para que al buscar un resource en la ArrayList con "contains", sirva porque si no están
    // en el mismo espacio de memoria entonces se trata como otro elemento diferente
    public boolean equals(Object other) {
        if (this == other) return true;
        // si es de otra clase o el otro es null entonces no
        if (other == null || getClass() != other.getClass()) return false;
        // casting para revisar id
        Resource otherResource = (Resource) other;
        return this.id.equals(otherResource.getId());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
