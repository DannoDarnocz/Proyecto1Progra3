package resourcemanager.service;

import resourcemanager.logic.CategoryLogic;
import resourcemanager.model.Category;

import java.util.ArrayList;

public class CategoryService {
    private CategoryLogic categoryLogic=new CategoryLogic();

    public ArrayList<Category> findFreeCategories() throws Exception{
        return categoryLogic.findFreeCategories();
    }

    public ArrayList<Category> getAllCategories() throws Exception{
        return categoryLogic.getAllCategories();
    }


    public ArrayList<String> convertListToIds(ArrayList<Category> categories){
        return categoryLogic.convertListToIds(categories);
    }

    public boolean deleteCategory(String id) throws Exception {
        return categoryLogic.deleteCategory(id);
    }

    public Category searchById(String id) throws Exception {
        return categoryLogic.searchById(id);
    }
    public Category searchByDescription(String desc) throws Exception {
        return categoryLogic.searchByDescription(desc);
    }

    public boolean updateCategory(Category c) throws Exception {
        return categoryLogic.updateCategory(c);
    }

    public void printAllCategories() throws Exception {
        categoryLogic.printAllCategories();
    }

    public Category addCategory(String description) throws Exception{
        return categoryLogic.addCategory(description);
    }
}
