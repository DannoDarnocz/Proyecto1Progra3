package resourcemanager.logic;

import resourcemanager.data.LoadFromXML;
import resourcemanager.model.Category;

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
}
