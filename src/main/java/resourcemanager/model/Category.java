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

    @Override
    // esto es para que al buscar una category en la ArrayList con "contains", sirva porque si no están
    // en el mismo espacio de memoria entonces se trata como otro elemento diferente
    public boolean equals(Object other) {
        if (this == other) return true;
        // si es de otra clase o el otro es null entonces no
        if (other == null || getClass() != other.getClass()) return false;
        // casting para revisar id
        Category otherResource = (Category) other;
        return this.id.equals(otherResource.getId());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
