package resourcemanager.logic;

import javafx.stage.Stage;
import resourcemanager.data.LoadFromXML;
import resourcemanager.data.SaveToXML;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;
import resourcemanager.logic.PrintLogic;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import java.io.File;
import java.security.InvalidParameterException;
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

        if (currentTarget == null) {
            throw new InstanceNotFoundException("No se encontró una categoría con ese ID");
        }
        if (updatedTarget.getDescription() == null || updatedTarget.getDescription().isBlank()) {
            throw new InvalidParameterException("La descripción no puede estar vacía");
        }

        if (haveNumbers(updatedTarget.getDescription())) {throw new InvalidParameterException("La descripción no puede tener números");}


        String descNorm = normalizeDescription(updatedTarget.getDescription());

        if (descNorm.equals(currentTarget.getDescription())) {
            // no hay cambio, no se puede
            throw new InstanceAlreadyExistsException("La descripción de la categoría es la misma");
        }

        Category catNormalizada = new Category(updatedTarget.getId(), descNorm);
        return SaveToXML.updateCategory(catNormalizada);
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

    public static void printAllCategories() throws Exception {
        // obtener lista de todas las categorias
        ArrayList<Category> allCategories = getAllCategories();

        // enviar a que la clase de impresion se encargue de generar el pdf
        File pdf = PrintLogic.generatePdf(allCategories, Category.class, "lista_de_categorias.pdf");
        PrintLogic.openPdf(pdf);
    }

    public static Category addCategory(String description) throws Exception {
        if (description == null || description.isBlank()){
            throw new InvalidParameterException("La descripción no puede estar vacía");
        }
        if (haveNumbers(description)) {throw new InvalidParameterException("La descripción no puede tener números");}

        String descNorm = normalizeDescription(description);

        //Obtiene todas las categorias
        ArrayList<Category> allCat = LoadFromXML.loadCategories();

        //Verifica que no exista ya dicha categoria
        for (Category c : allCat){
            if (c.getDescription().equalsIgnoreCase(descNorm)){
                throw new InstanceAlreadyExistsException("Ya existe una categoría con esa descripción");
            }
        }

        //Usa regex para extraer los digitos y selecciona el máximo de todas las id para evitar duplicaciones
        int maxN = 0;
        for (Category c : allCat){
            String digits = c.getId().replaceAll("\\D+", "");
            if (!digits.isEmpty()) maxN = Math.max(maxN,Integer.parseInt(digits));
        }

        //Auto genera las id nuevas y las agrega a la base de datos.
        String newId = "cat" + (maxN + 1);
        Category newCat = new Category(newId,descNorm);
        allCat.add(newCat);
        SaveToXML.overwriteCategories(allCat);

        return newCat;
    }

    // normaliza el formato del texto
    // (ej: "SALA DE juntas" o "sala   de JUNTAS" -> "Sala De Juntas")
    private static String normalizeDescription(String texto) {
        String clean = texto.trim();
        if (clean.isEmpty()) return clean;

        String[] palabras = clean.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i];
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1).toLowerCase());
            }
            if (i < palabras.length - 1) resultado.append(" ");
        }

        return resultado.toString();
    }
    // true si el texto tiene al menos un dígito (0-9) en cualquier parte
    private static boolean haveNumbers(String texto) {
        for (char c : texto.toCharArray()) {
            if (Character.isDigit(c)) return true;
        }
        return false;
    }
}
