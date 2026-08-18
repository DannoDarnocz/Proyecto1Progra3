package resourcemanager.structure;

import resourcemanager.Category;

import java.util.ArrayList;

public class CategoryList {
    private ArrayList<Category> categories;

    public CategoryList(){
        categories = new ArrayList<Category>();
    }

    public void add(Category r){
        categories.add(r);
    }
}
