package resourcemanager.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Text;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;
import resourcemanager.service.CategoryService;
import resourcemanager.model.Category;
import resourcemanager.service.CategoryService;
import resourcemanager.service.ResourceService;

import java.util.ArrayList;

public class ResourcesTabController {

    @FXML
    private ChoiceBox cb_resource_filter_category;

    @FXML
    private TextField txt_resource_search_description;

    @FXML
    private Button btn_resource_save;
    @FXML
    private Button btn_resource_delete;
    @FXML
    private Button btn_resource_clear;
    @FXML
    private Button btn_resource_search;

    @FXML
    private TableView tbl_resource_list;

    // para borrar text fields y deshabilitar los botones
    private void resetDefaultStates(){
        // ya no hay categoria encontrada asi que hay que buscar de nuevo antes de poder hacer cambios o eliminar
        btn_resource_save.setDisable(true);
        btn_resource_delete.setDisable(true);

        // resetear text fields
        txt_resource_search_description.clear();
    }

    private void reloadCategories(){
        TableColumn<Resource, String> columnId = (TableColumn<Resource, String>) tbl_resource_list.getColumns().get(0);
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Resource, String> columnCategoryId = (TableColumn<Resource, String>) tbl_resource_list.getColumns().get(1);
        columnCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        columnCategoryId.setStyle("-fx-alignment: CENTER;");


        TableColumn<Resource, String> columnDescription = (TableColumn<Resource, String>) tbl_resource_list.getColumns().get(2);
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnDescription.setStyle("-fx-alignment: CENTER;");

        // buscar categorias
        try{
            // buscar todas las categorias y meterlas como filas en la tabla
            ArrayList<Resource> availableResources = ResourceService.getAllResources();
            tbl_resource_list.getItems().setAll(availableResources);

        } catch (Exception e) {
            Utilities.showAlert("Error","Se produjo un error al cargar los recursos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void initialize() {
        // agregar categorias disponibles en el combobox
        reloadCategories();
        try {
            cb_resource_filter_category.setValue("Seleccionar");
            ArrayList<Category> availableCategories = CategoryService.getAllCategories();
            // convertir todas las categorias a solo IDs
            ArrayList<String> strCategories = CategoryService.convertListToIds(availableCategories);
            cb_resource_filter_category.getItems().setAll(strCategories);
        } catch (Exception e) {
            // todo: aslkdjaslkd
        }


        btn_resource_search.setOnAction(event -> {
            // obtener categoria seleccionada
            String categoryId = cb_resource_filter_category.getValue().toString();
            String resourceDescription = txt_resource_search_description.getText();

            if(categoryId == "Seleccionar"){
                Utilities.showAlert("Error","Debe de seleccionar una categoría y la descripción del recurso", Alert.AlertType.ERROR);
            }
            else{
                System.out.println(categoryId);
            }
        });


    }
}
