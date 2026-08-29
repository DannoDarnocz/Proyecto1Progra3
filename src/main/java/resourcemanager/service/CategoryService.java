package resourcemanager.service;

import resourcemanager.data.DataHandler;
import resourcemanager.model.Category;

import javax.xml.crypto.Data;
import java.util.ArrayList;

public class CategoryService {
    public static ArrayList<Category> findFreeCategories() throws Exception{
        return DataHandler.findFreeCategories();
    }

    public static ArrayList<String> convertListToIds(ArrayList<Category> categories){
        return CategoryService.convertListToIds(categories);
    }
}
