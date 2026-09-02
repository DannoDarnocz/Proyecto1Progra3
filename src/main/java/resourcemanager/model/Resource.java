package resourcemanager.model;


import resourcemanager.logic.CategoryLogic;
import resourcemanager.logic.ResourceLogic;

public class Resource {
    private String id;
    private String categoryId;
    private String description;

    public Resource(String id, String category, String description){
        this.id=id;
        this.categoryId = category;
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
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String category) throws Exception {
        // verificar que exista
        Category c = CategoryLogic.findCategoryById(category);
        if (c != null) this.categoryId = category;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
