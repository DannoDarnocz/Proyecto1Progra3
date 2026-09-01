package resourcemanager.service;

import javafx.stage.Stage;
import resourcemanager.data.DataHandler;
import resourcemanager.logic.CategoryLogic;
import resourcemanager.model.Category;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class CategoryService {
    public static ArrayList<Category> findFreeCategories() throws Exception{
        return DataHandler.findFreeCategories();
    }

    public static ArrayList<Category> getAllCategories() throws Exception{
        return CategoryLogic.getAllCategories();
    }


    public static ArrayList<String> convertListToIds(ArrayList<Category> categories){
        return CategoryLogic.convertListToIds(categories);
    }

    public static boolean deleteCategory(String id) throws Exception {
        return CategoryLogic.deleteCategory(id);
    }

    public static Category searchById(String id) throws Exception {
        return CategoryLogic.searchById(id);
    }
    public static Category searchByDescription(String desc) throws Exception {
        return CategoryLogic.searchByDescription(desc);
    }

    public static boolean updateCategory(Category c) throws Exception {
        return CategoryLogic.updateCategory(c);
    }

    public static void printAllCategories() throws Exception {
        CategoryLogic.printAllCategories();
    }
}
