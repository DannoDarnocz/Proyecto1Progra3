package resourcemanager.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import resourcemanager.model.Category;
import resourcemanager.model.Resource;
import resourcemanager.service.CategoryService;
import resourcemanager.service.ResourceService;
import javax.management.InstanceAlreadyExistsException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class ResourcesTabController {

    @FXML
    private ChoiceBox<Category> cb_resource_filter_category;
    @FXML
    private ChoiceBox<Category> cb_resource_category;

    @FXML
    private TextField txt_resource_search_description;
    @FXML
    private TextField txt_resource_description;
    @FXML
    private TextField txt_resource_id;

    @FXML
    private Button btn_resource_save;
    @FXML
    private Button btn_resource_delete;
    @FXML
    private Button btn_resource_clear;
    @FXML
    private Button btn_resource_search;
    @FXML
    private Button btn_resource_print;

    @FXML
    private TableView tbl_resource_list;

    // para borrar text fields y deshabilitar los botones
    private void resetDefaultStates(){
        // ya no hay categoria encontrada asi que hay que buscar de nuevo antes de poder hacer cambios o eliminar
        btn_resource_delete.setDisable(true);

        // resetear text fields
        txt_resource_search_description.clear();
        txt_resource_id.clear();
        txt_resource_description.clear();
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
            reloadCategoryChoices();
        } catch (Exception e) {
            Utilities.showAlert("Error","Se produjo un error al cargar los recursos", Alert.AlertType.ERROR);
        }
    }

    private void reloadCategoryChoices(){
        try {
            ArrayList<Category> availableCategories = CategoryService.getAllCategories();
            // obtener categorias como arraylist y luego convertirlas a observablelist para meterlas en el choicebox
            ArrayList<Category> categories = CategoryService.getAllCategories();
            ObservableList<Category> categoriesObservable = FXCollections.observableArrayList(categories);
            if (categoriesObservable.isEmpty()) {
                // no hay categorias, no se puede filtrar ni asignar categoria a un recurso
                cb_resource_filter_category.getItems().clear();
                cb_resource_category.getItems().clear();
                cb_resource_filter_category.setDisable(true);
                cb_resource_category.setDisable(true);
                //Desabilita los ChoiceBox al no haber forma de poder realizar recursos sin categorias
            } else {
                cb_resource_category.setItems(categoriesObservable);
                cb_resource_filter_category.setItems(categoriesObservable);
                cb_resource_filter_category.setDisable(false);
                cb_resource_category.setDisable(false);
            }
        } catch (Exception e) {
            Utilities.showAlert("Error","No existen categorias disponibles",Alert.AlertType.ERROR);
            cb_resource_filter_category.setDisable(true);
            cb_resource_category.setDisable(true);
            //Desabilita los ChoiceBox al no haber forma de poder realizar recursos sin categorias
        }
    }
    public void refreshCategoryChoices(){
        reloadCategoryChoices();
    }

    @FXML
    private void initialize() {
        //En caso de recursos de descripciones y categorias repetidas, se selecciona directamente de la tabla basandose en el ID
        tbl_resource_list.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Resource seleccionado = (Resource) newValue;

                txt_resource_id.setText(seleccionado.getId());
                txt_resource_description.setText(seleccionado.getDescription());

                btn_resource_save.setDisable(false);
                btn_resource_delete.setDisable(false);
            }
        });
        // agregar categorias disponibles en el combobox
        btn_resource_delete.setDisable(true);
        txt_resource_id.setDisable(true);
        reloadCategories();

        btn_resource_search.setOnAction(event -> {
            String categoryId = cb_resource_filter_category.getValue().toString();
            String resourceDescription = txt_resource_search_description.getText().trim();

            if(categoryId == "Seleccionar" || resourceDescription.isEmpty()){
                Utilities.showAlert("Error","Debe de seleccionar una categoría y la descripción del recurso", Alert.AlertType.ERROR);
                return;
            }
            else{
                try{
                    // buscar recurso por descripcion (el usuario no deberia de aprenderse el id porque es algo arbitrario para identificarlas)
                    Resource foundResource = ResourceService.searchByDescriptionAndCategory(resourceDescription, categoryId);

                    if(foundResource==null){
                        // no se encontro el recurso
                        Utilities.showAlert("Error","No se ha encontrado el recurso.", Alert.AlertType.ERROR);
                    }
                    else{
                        // reemplazar datos de los text field con lo que se encontró
                        String id = foundResource.getId();
                        String description = foundResource.getDescription();

                        txt_resource_id.setText(id);
                        txt_resource_description.setText(description);
                        Category foundCategory = CategoryService.searchById(foundResource.getCategoryId());

                        cb_resource_category.setValue(foundCategory);

                        // ahora el usuario puede cambiar datos o eliminarla
                        btn_resource_save.setDisable(false);
                        btn_resource_delete.setDisable(false);

                    }
                } catch (Exception e) {
                    Utilities.showAlert("Error","Ha ocurrido un error al buscar la categoria: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        btn_resource_save.setOnAction(event -> {
            String id = txt_resource_id.getText().trim();
            String description = txt_resource_description.getText().trim();
            Object categoryValue = cb_resource_category.getValue();
            String categoryId = categoryValue != null ? categoryValue.toString() : null;

            if(description.isEmpty()){
                Utilities.showAlert("Error", "Debe de escribir una descripción para el recurso.", Alert.AlertType.ERROR);
                return;
            }
            if(categoryId == null || categoryId.equals("Seleccionar")){
                Utilities.showAlert("Error", "Debe de seleccionar una categoría para el recurso.", Alert.AlertType.ERROR);
                return;
            }
            try {
                if (id.isEmpty()) {
                    // no hay id cargado, recurso nuevo
                    Resource nuevo = ResourceService.addResource(categoryId, description);
                    Utilities.showAlert("Confirmacion", "Se ha agregado el recurso " + nuevo.getId(), Alert.AlertType.INFORMATION);
                    resetDefaultStates();
                } else {
                    // hay un id cargado, se esta editando
                    Resource resourceDTO = new Resource(id, categoryId, description);
                    if (ResourceService.updateResource(resourceDTO)) {
                        Utilities.showAlert("Confirmacion", "Se ha actualizado el recurso correctamente", Alert.AlertType.INFORMATION);
                    } else {
                        Utilities.showAlert("Error", "No se ha encontrado el recurso.", Alert.AlertType.ERROR);
                    }
                }
                reloadCategories();
            } catch (InvalidParameterException | InstanceAlreadyExistsException e){
                Utilities.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                Utilities.showAlert("Error", "Ha ocurrido un error al guardar el recurso: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // limpiar todos los datos introducidos y los de la categoria encontrada
        btn_resource_clear.setOnAction(event -> {
            resetDefaultStates();
        });

        btn_resource_delete.setOnAction(event -> {
            String id = txt_resource_id.getText().trim();

            // debe de buscar un recurso primero
            if(id.isEmpty()){
                Utilities.showAlert("Error","Debe de buscar  un recurso para eliminarla.", Alert.AlertType.ERROR);
            }
            else{
                // pedir confirmacion
                Alert confirmation = Utilities.showAlert("Aviso","Desea borrar el recurso con el ID " + id + "?", Alert.AlertType.CONFIRMATION);

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // borrar
                        try{
                            if(ResourceService.deleteResource(id)){
                                Utilities.showAlert("Confirmacion","Se ha borrado el recurso correctamente", Alert.AlertType.INFORMATION);
                                resetDefaultStates();
                                // recargar tabla de categorias
                                reloadCategories();
                            }
                            else{
                                Utilities.showAlert("Error","El recurso ingresado no se pudo borrar debido a que no se encontró en la lista.", Alert.AlertType.ERROR);
                            }
                        } catch (Exception e) {
                            Utilities.showAlert("Error","Ha ocurrido un error al borrar el recurso: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                });
            }
        });

        btn_resource_print.setOnAction(event-> {
            // enviar a capa servicios que dirige a logica para luego delegarle a la de imprimir la tarea de imprimir
            try{
                ResourceService.printAllCategories();
            } catch (Exception e) {
                Utilities.showAlert("Error","Se ha producido un error al generar el PDF para impresión:  "+e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
