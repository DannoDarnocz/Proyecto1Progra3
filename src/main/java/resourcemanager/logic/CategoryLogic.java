package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.data.SaveToXML;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;

import javax.management.InstanceAlreadyExistsException;
import java.util.ArrayList;

public class CategoryLogic {
    // buscar categoria por id
    public static Category findCategoryById(String id) throws Exception {
        ArrayList<Category> allCategories = LoadFromXML.loadCategories();


        // recorrer todas las categorias hatsa encontra una con esa id
        for(Category c : allCategories) {
            System.out.println("id categoria actual: "+c.getId() + " id de la categoria que se busca: "+ id);
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    // convertir lista de categorias a lista de strings con id
    public static ArrayList<String> convertListToIds(ArrayList<Category> categories) {
        ArrayList<String> categoryStrings = new ArrayList<>();

        for (Category c : categories) {
            categoryStrings.add(c.getId());
        }

        if (categories.isEmpty()) return null;
        return categoryStrings;
    }

    public static ArrayList<Category> getAllCategories() throws Exception {
        return LoadFromXML.loadCategories();
    }

    public static Category searchById(String id) throws Exception {
        ArrayList<Category> allCategories = LoadFromXML.loadCategories();

        for(Category c : allCategories){
            if(c.getId().equals(id)) return c;
        }

        // no se encontro
        return null;
    }

    public static boolean deleteCategory(String id) throws Exception {
        Category c = searchById(id);
        if(c==null) return false;

        // no se puede eliminar si tiene al menos un recurso asociado a la categoria
        if(!categoryIsOrphan(c)){
            // la categoria tiene asociado un recurso actualmente
            throw new RuntimeException("Existe al menos una reserva asociada con esta categoría.");
        }

        // obtener lista completa y remover ese en especifico
        ArrayList<Category> allCategories = LoadFromXML.loadCategories();
        allCategories.remove(c);

        // sobreescribir archivo
        SaveToXML.overwriteCategories(allCategories);
        return true;
    }

    public static Category searchByDescription(String description) throws Exception {
        ArrayList<Category> allCategories = LoadFromXML.loadCategories();

        // convertir a minuscula
        description = description.toLowerCase();

        for(Category c : allCategories){
            // convertir descripcion del actual a minuscula tambien
            String currentDesc = c.getDescription().toLowerCase();
            if( currentDesc.equals(description)) return c;
        }

        // no se encontro
        return null;
    }

    public static boolean updateCategory(Category updatedTarget) throws Exception{
        Category currentTarget = searchById(updatedTarget.getId()); // asumiendo que el id nunca deberia cambiar

        if(updatedTarget.getDescription().equals(currentTarget.getDescription())){
            // no hay cambio, no se puede
            throw new InstanceAlreadyExistsException("La descripción de la categoría es la misma");
        }

        return SaveToXML.updateCategory(updatedTarget);
    }

    public static boolean categoryIsOrphan(Category target) throws Exception {
        // obtener lista de recursos. si alguna tiene asociada la categoria
        // si tiene asociado un recurso entonces no se puede borrar (si no tiene recurso asociado entonces no tiene reserva asociada tampoco)
        ArrayList<Resource> allResources = LoadFromXML.loadResources();
        String targetId = target.getId();

        for(Resource r : allResources){
            if(r.getCategoryId().equals(targetId)) return false; // tiene un recurso asociado
        }

        // ningún recurso tiene esa categoría;
        return true;
    }
}
