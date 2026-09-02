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
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.User;
import resourcemanager.model.dto.ReservationDTO;
import resourcemanager.model.dto.GeneratedReservationDTO;

import resourcemanager.service.CategoryService;
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

    private void reloadCurrentReservations(){
        // quitar lo que ya haya
        tbl_reserve_current.getItems().clear();

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
    }

    private void reloadCategories(){
        try{
            // quitar lo que ya haya
            tbl_reserve_categories.getItems().clear();
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
            Utilities.showAlert("Error","Ha ocurrido un error al obtener las categorías disponibles: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    // aplica al formulario lo que la IA logro extraer de la frase en lenguaje natural.
    // solo toca los campos que la IA pudo llenar, el resto los deja como estaban
    private void applyAiSuggestion(GeneratedReservationDTO generated) {
        if (generated.getDescription() != null && !generated.getDescription().isBlank()) {
            txt_reserve_activity.setText(generated.getDescription());
        }

        if (generated.getDate() != null) {
            dt.setValue(generated.getDate());
        }

        if (generated.getStartHour() != null) {
            spn_reserve_hour_start.getValueFactory().setValue(clampHour(generated.getStartHour()));
        }
        if (generated.getEndHour() != null) {
            spn_reserve_hour_end.getValueFactory().setValue(clampHour(generated.getEndHour()));
        }

        // seleccionar en la tabla las categorias que la IA identifico y que ademas siguen disponibles
        TableView.TableViewSelectionModel selectionModel = tbl_reserve_categories.getSelectionModel();
        selectionModel.clearSelection();

        ArrayList<Category> sugeridas = generated.getCategories();
        if (sugeridas != null) {
            ObservableList<Category> disponibles = tbl_reserve_categories.getItems();
            for (Category sugerida : sugeridas) {
                for (Category disponible : disponibles) {
                    if (disponible.getId().equals(sugerida.getId())) {
                        selectionModel.select(disponible);
                        break;
                    }
                }
            }
        }

        // avisar si quedo algo sin llenar, para que el usuario sepa que debe completarlo a mano
        StringBuilder faltantes = new StringBuilder();
        if (generated.getDescription() == null) faltantes.append("- Actividad\n");
        if (generated.getDate() == null) faltantes.append("- Fecha\n");
        if (generated.getStartHour() == null) faltantes.append("- Hora de inicio\n");
        if (generated.getEndHour() == null) faltantes.append("- Hora de fin\n");
        if (sugeridas == null || sugeridas.isEmpty()) faltantes.append("- Categorías\n");

        if (faltantes.isEmpty()) {
            Utilities.showAlert("Confirmación", "Se llenó el formulario por IA. Puede revisarlo antes de reservar.", Alert.AlertType.INFORMATION);
        } else {
            Utilities.showAlert("Revise el formulario", "La IA no pudo determinar lo siguiente, complételo manualmente:\n" + faltantes, Alert.AlertType.WARNING);
        }
    }

    private int clampHour(int hour) {
        if (hour < 0) return 0;
        if (hour > 23) return 23;
        return hour;
    }


    @FXML
    private void initialize() {
        //Politicas para que tabla no tenga espacio sin usar
        tbl_reserve_categories.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbl_reserve_current.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Habilitación del Spinner y hora por defecto
        spn_reserve_hour_start.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        spn_reserve_hour_end.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 1));

        // --- INICIALIZAR TABLA DE CATEGORIAS
        reloadCategories();

        // --- INCIIALIZAR TABLA DE RESERVAS ACTUALES
        reloadCurrentReservations();

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
                            User currentUser = UserService.getLoggedUser();
                            ReservationService.deleteReservation(selected.getId(),currentUser);

                            Alert alert = Utilities.showAlert("Confirmacion","Se ha eliminado la reserva", Alert.AlertType.INFORMATION);

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
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.atStartOfDay();

                    ReservationDTO dto = new ReservationDTO(description, start, end);
                    try {
                        // lógica maneja la creación y asignación de recursos
                        User currentUser = UserService.getLoggedUser();
                        Reservation r = ReservationService.createReservationForUser(dto, selectedItems, currentUser);

                        Utilities.showAlert("Confirmacion", "Se ha reservado con exito", Alert.AlertType.INFORMATION);

                        // quitar seleccion
                        selectionModel.clearSelection();

                        // actualizar tablas
                        reloadCategories();
                        reloadCurrentReservations();
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
                return;
            }
            btn_reserve_ai.setDisable(true);
            btn_reserve_ai.setText("Extrayendo...");

            ReservationService.promptAI(
                    prompt,
                    generated -> {
                        btn_reserve_ai.setDisable(false);
                        btn_reserve_ai.setText("Extraer con IA");
                        applyAiSuggestion(generated);
                        },
                    error -> {
                        btn_reserve_ai.setDisable(false);
                        btn_reserve_ai.setText("Extraer con IA");
                        String mensaje = error.getMessage() != null ? error.getMessage() : "Ha ocurrido un error inesperado";
                        Utilities.showAlert("Error", mensaje, Alert.AlertType.ERROR);
                    }
                );
        });

        btn_reserve_print.setOnAction(event -> {
            try{
                User currentUser = UserService.getLoggedUser();
                UserService.printUserReservations(currentUser);
            } catch (Exception e) {
                Utilities.showAlert("Error","Se ha producido un error al generar el PDF para impresión:  "+e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
