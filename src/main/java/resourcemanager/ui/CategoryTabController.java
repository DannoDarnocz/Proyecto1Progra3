package resourcemanager.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.PropertyValueFactory;
import resourcemanager.model.Category;
import resourcemanager.service.CategoryService;

import javax.management.InstanceAlreadyExistsException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class CategoryTabController {
    @FXML
    private TextField txt_category_search_description;

    @FXML
    private Button btn_category_search;

    @FXML
    private Button btn_category_print;

    @FXML
    private TextField txt_category_id;

    @FXML
    private TextField txt_category_description;

    @FXML
    private Button  btn_category_save;

    @FXML
    private Button btn_category_clear;

    @FXML
    private Button btn_category_delete;

    @FXML
    private TableView tbl_category_list;

    private CategoryService categoryService = new CategoryService();

    // para borrar text fields y deshabilitar los botones
    private void resetDefaultStates(){
        // ya no hay categoria encontrada asi que hay que buscar de nuevo antes de poder hacer cambios o eliminar
        btn_category_delete.setDisable(true);

        // resetear text fields
        txt_category_id.clear();
        txt_category_description.clear();
        txt_category_search_description.clear();
    }

    private void reloadCategories(){
        TableColumn<Category, String> columnId = (TableColumn<Category, String>) tbl_category_list.getColumns().get(0);
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Category, String> columnDescription = (TableColumn<Category, String>) tbl_category_list.getColumns().get(1);
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnDescription.setStyle("-fx-alignment: CENTER;");

        // buscar categorias
        try{
            // buscar todas las categorias y meterlas como filas en la tabla
            ArrayList<Category> availableCategories = categoryService.getAllCategories();
            tbl_category_list.getItems().setAll(availableCategories);
        } catch (Exception e) {
            Utilities.showAlert("Error","Se produjo un error al cargar las categorías", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void initialize() {
        // apenas el usuario ingresa el boton de guardar cambios y borrar categoria esta deshabilitado porque primero tiene que buscar una valida
        btn_category_delete.setDisable(true);

        // mostrar categorias disponibles en la tabla
        reloadCategories();

        btn_category_search.setOnAction(event -> {
            String categoryDescription = txt_category_search_description.getText().trim();
            if(categoryDescription.isEmpty()){
                // no puede estar vacio
                Utilities.showAlert("Error","Debe de escribir la descripcion de la categoria a buscar", Alert.AlertType.ERROR);
            }
            else{
                try{
                    // buscar categoria por descripcion (el usuario no deberia de aprenderse el id porque es algo arbitrario para identificarlas)
                    Category foundCategory = categoryService.searchByDescription(categoryDescription);

                    if(foundCategory==null){
                        // no se encontro la categoria
                        Utilities.showAlert("Error","No se ha encontrado la categoría.", Alert.AlertType.ERROR);
                    }
                    else{
                        // reemplazar datos de los text field con lo que se encontró
                        String id = foundCategory.getId();
                        String description = foundCategory.getDescription();

                        txt_category_id.setText(id);
                        txt_category_description.setText(description);

                        // ahora el usuario puede cambiar datos o eliminarla
                        btn_category_save.setDisable(false);
                        btn_category_delete.setDisable(false);

                    }
                } catch (Exception e) {
                    Utilities.showAlert("Error","Ha ocurrido un error al buscar la categoria: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        btn_category_save.setOnAction(event -> {
            String id = txt_category_id.getText().trim();
            String description = txt_category_description.getText().trim();

            if(description.isEmpty()){
                Utilities.showAlert("Error", "Debe de escribir una descripción para la categoría.", Alert.AlertType.ERROR);
                return;
            }
            try {
                if (id.isEmpty()) {
                    // no hay id cargado, categoria nueva
                    Category nueva = categoryService.addCategory(description);
                    Utilities.showAlert("Confirmacion", "Se ha agregado la categoría " + nueva.getId(), Alert.AlertType.INFORMATION);
                    resetDefaultStates();
                } else {
                    // hay un id cargado, se esta editando
                    Category categoryDTO = new Category(id, description);
                    if (categoryService.updateCategory(categoryDTO)) {
                        Utilities.showAlert("Confirmacion", "Se ha actualizado la categoria correctamente", Alert.AlertType.INFORMATION);
                    } else {
                        Utilities.showAlert("Error", "No se ha encontrado la categoría.", Alert.AlertType.ERROR);
                    }
                }
                reloadCategories();
            } catch (InvalidParameterException | InstanceAlreadyExistsException e){
                Utilities.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
            } catch (Exception e) {
                Utilities.showAlert("Error", "Ha ocurrido un error al guardar la categoria: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // limpiar todos los datos introducidos y los de la categoria encontrada
        btn_category_clear.setOnAction(event -> {
            resetDefaultStates();
        });

        btn_category_delete.setOnAction(event -> {
            String id = txt_category_id.getText().trim();

            // debe de buscar categoria primero, aunque el boton deberia de estar deshabilitado. por si acaso verificar
            if(id.isEmpty()){
                Utilities.showAlert("Error","Debe de buscar una categoria para eliminarla.", Alert.AlertType.ERROR);
            }
            else{
                // pedir confirmacion
                Alert confirmation = Utilities.showAlert("Aviso","Desea borrar la categoría con el ID " + id + "?", Alert.AlertType.CONFIRMATION);

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // borrar
                        try{
                            if(categoryService.deleteCategory(id)){
                                Utilities.showAlert("Confirmacion","Se ha borrado la categoria correctamente", Alert.AlertType.INFORMATION);
                                resetDefaultStates();
                                // recargar tabla de categorias
                                reloadCategories();
                            }
                            else{
                                Utilities.showAlert("Error","La categoria ingresada no se pudo borrar debido a que no se encontró en la lista.", Alert.AlertType.ERROR);
                            }
                        } catch (Exception e) {
                            Utilities.showAlert("Error","Ha ocurrido un error al borrar la categoria: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                });
            }
        });

        btn_category_print.setOnAction(event-> {
            // enviar a capa servicios que dirige a logica para luego delegarle a la de imprimir la tarea de imprimir
            try{
                categoryService.printAllCategories();
            } catch (Exception e) {
                Utilities.showAlert("Error","Se ha producido un error al generar el PDF para impresión:  "+e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
