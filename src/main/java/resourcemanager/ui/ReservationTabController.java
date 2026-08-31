package resourcemanager.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import resourcemanager.data.DataHandler;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.model.dto.ReservationDTO;
import resourcemanager.service.CategoryService;
import resourcemanager.service.GeminiService;
import resourcemanager.service.ReservationService;
import resourcemanager.service.UserService;
import resourcemanager.structure.CurrentSession;

import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReservationTabController {
    @FXML
    private TextArea txt_reserve_prompt;
    @FXML
    private Button btn_reserve_print;
    @FXML
    private Button btn_reserve_ai;
    @FXML
    private TextField txt_reserve_activity;
    @FXML
    private DatePicker dt;
    @FXML
    private Spinner spn_reserve_hour_start;
    @FXML
    private Spinner spn_reserve_hour_end;
    @FXML
    private TableView tbl_reserve_categories;
    @FXML
    private TableView tbl_reserve_current;
    @FXML
    private Button btn_reserve_confirm;
    @FXML
    private Button btn_clear11;
    @FXML
    private Button btn_reserve_delete;
    @FXML
    private Button btn_reserve_cancel;

    @FXML
    private void initialize() {
        //Politicas para que tabla no tenga espacio sin usar
        tbl_reserve_categories.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbl_reserve_current.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Habilitación del Spinner y hora por defecto
        spn_reserve_hour_start.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        spn_reserve_hour_end.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 1));

        // --- INICIALIZAR TABLA DE CATEGORIAS
        try{
            // tomar las columnas que ya existen en el FXML, en el mismo orden en que aparecen ahi (Id, Descripcion)
            TableColumn<Category, String> columnId = (TableColumn<Category, String>) tbl_reserve_categories.getColumns().get(0);
            columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
            columnId.setStyle("-fx-alignment: CENTER;");

            TableColumn<Category, String> columnDescription = (TableColumn<Category, String>) tbl_reserve_categories.getColumns().get(1);
            columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            columnDescription.setStyle("-fx-alignment: CENTER;");

            // para obtener modelo de seleccion
            TableView.TableViewSelectionModel selectionModel =
                    tbl_reserve_categories.getSelectionModel();
            //Para seleccionar varias
            selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

            //Se pueden buscar varias
            ArrayList<Category> availableCategories = CategoryService.findFreeCategories();
            System.out.print(availableCategories.size());

            tbl_reserve_categories.getItems().setAll(availableCategories);

        } catch (Exception e){
            e.printStackTrace();
        }


        // --- INCIIALIZAR TABLA DE RESERVAS ACTUALES

        // tomar las columnas que ya existen en el FXML, en el mismo orden en que aparecen ahi (Id, Descripcion)
        TableColumn<Reservation, String> columnId = (TableColumn<Reservation, String>) tbl_reserve_current.getColumns().get(0);
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnId.setStyle("-fx-alignment: CENTER;");

        TableColumn<Reservation, String> columnDescription = (TableColumn<Reservation, String>) tbl_reserve_current.getColumns().get(1);
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnDescription.setStyle("-fx-alignment: CENTER;");

        TableColumn<Reservation, LocalDateTime> columnStart = (TableColumn<Reservation, LocalDateTime>) tbl_reserve_current.getColumns().get(2);
        columnStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        columnStart.setStyle("-fx-alignment: CENTER;");

        TableColumn<Reservation, LocalDateTime> columnEnd = (TableColumn<Reservation, LocalDateTime>) tbl_reserve_current.getColumns().get(3);
        columnEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        columnEnd.setStyle("-fx-alignment: CENTER;");

        // obtener el usuario que inició sesión para obtener sus reservas
        User currentUser = UserService.getLoggedUser();
        ArrayList<Reservation> userReservations = UserService.findReservationsForUser(currentUser);

        // agregar todas las encontradas
        if(userReservations!=null){
            tbl_reserve_current.getItems().setAll(userReservations);
        }
        else{
            System.out.println("NO HAY RESERVAS!!!!"); // TODO ELIMINAR
        }

        // cancelar reserva seleccionada
        btn_reserve_cancel.setOnAction(event -> {
            // para obtener seleccionada
            TableView.TableViewSelectionModel selectionModel =
                    tbl_reserve_current.getSelectionModel();
            // solo se puede seleccionar 1
            selectionModel.setSelectionMode(
                    SelectionMode.SINGLE);

            // obtener fila seleccionada
            ObservableList<Reservation> selectedItems =
                    selectionModel.getSelectedItems();

            if(selectedItems.isEmpty()){
                Utilities.showAlert("Error","Debe de seleccionar una fila", Alert.AlertType.ERROR);
            }
            else{
                Reservation selected = selectedItems.getFirst(); // como es solo 1, deberia solo estar ahi
                Alert confirmation = Utilities.showAlert("Aviso","Desea borrar?", Alert.AlertType.CONFIRMATION);

                confirmation.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        try{
                            // TODO: en el xml no se borra por alguna razon
                            // eliminar la reserva del usuario y de la lista general
                            ReservationService.deleteReservation(selected.getId(),currentUser);

                            Alert alert = Utilities.showAlert("Confirmacion","Se ha eliminado la reserva", Alert.AlertType.INFORMATION);

                            // todo: borrarlo del sistema en el xml
                            // quitar fila
                            tbl_reserve_current.getItems().remove(selected);

                            // quitar seleccion
                            selectionModel.clearSelection();
                        } catch (Exception e) {
                            Utilities.showAlert("Error","No se pudo eliminar", Alert.AlertType.ERROR);
                            e.printStackTrace();
                        }
                    }
                });
            }

        });

        btn_clear11.setOnAction(event -> {
            // para obtener modelo de seleccion
            TableView.TableViewSelectionModel selectionModel =
                    tbl_reserve_categories.getSelectionModel();
            selectionModel.setSelectionMode(
                    SelectionMode.SINGLE);
            // quitar seleccion
            selectionModel.clearSelection();
        });

        btn_reserve_confirm.setOnAction(event -> {

            System.out.println("quiere reservar");
            // obtener filas seleccionadas de categoria

            if(dt.getValue()==null || txt_reserve_activity.getText().isEmpty()){
                Utilities.showAlert("Error", "Debe llenar todos los espacios", Alert.AlertType.ERROR);
            }
            else {

                // obtener fila seleccionada
                TableView.TableViewSelectionModel selectionModel =
                        tbl_reserve_categories.getSelectionModel();
                ObservableList<Category> selectedItems =
                        selectionModel.getSelectedItems();

                if (selectedItems.isEmpty()) {
                    Utilities.showAlert("Error", "Debe de seleccionar al menos una categoria", Alert.AlertType.ERROR);
                } else {
                    // obtener datos
                    String description = txt_reserve_activity.getText();

                    // obtener fecha
                    LocalDate date = dt.getValue();
                    // construir LocalDateTime para guardarlo
                    // TODO: ARREGLAR SPINNER Y CAMBIAR HORA POR DEFECTO
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.atStartOfDay();

                    ReservationDTO dto = new ReservationDTO(description, start, end);
                    try {
                        // lógica maneja la creación y asignación de recursos
                        Reservation r = ReservationService.createReservationForUser(dto, selectedItems, currentUser);

                        Utilities.showAlert("Confirmacion", "Se ha reservado con exito", Alert.AlertType.INFORMATION);

                        // quitar seleccion
                        selectionModel.clearSelection();

                        // agregar nueva reserva a la tabla
                        tbl_reserve_current.getItems().add(r);
                    } catch (Exception e) {
                        Utilities.showAlert("Error","No se ha podido reservar. " + e.getMessage(), Alert.AlertType.ERROR);
                        e.printStackTrace();
                    }


                }

            }
        });


        btn_reserve_ai.setOnAction(event -> {
            String prompt = txt_reserve_prompt.getText().trim();
            if(prompt.isEmpty()){
                Utilities.showAlert("Error","Debe de describir la reserva", Alert.AlertType.ERROR);
            }
            else{
                try{
                    ReservationService.promptAI(prompt);
                    System.out.println("Waos"); //TODO BORRAR
                } catch(InvalidParameterException e){
                    Utilities.showAlert("Error",e.getMessage(), Alert.AlertType.ERROR);
                }catch (InterruptedException e){
                    Utilities.showAlert("Error","Se ha interrumpido el proceso de generación por IA: "+e.getMessage(), Alert.AlertType.ERROR);
                } catch (Exception e){
                    Utilities.showAlert("Error","Ha ocurrido un error: "+e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });


    }
}
