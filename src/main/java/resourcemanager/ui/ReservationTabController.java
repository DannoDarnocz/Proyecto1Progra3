package resourcemanager.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import resourcemanager.filehandler.DataFinder;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;
import resourcemanager.model.dto.ReservationDTO;
import resourcemanager.service.GeminiService;
import resourcemanager.structure.CurrentSession;

import javax.management.InstanceAlreadyExistsException;
import java.nio.file.FileAlreadyExistsException;
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
        // --- INICIALIZAR TABLA DE CATEGORIAS
        try{
            // buscar categorias con al menos 1 recurso dispónible
            ArrayList<Category> availableCategories = DataFinder.findFreeCategories();

            // crear columnas
            TableColumn<Category, String> columnId =
                    new TableColumn<>("ID");
            columnId.setCellValueFactory(
                    new PropertyValueFactory<>("id"));

            TableColumn<Category, String> columnDescription =
                    new TableColumn<>("Descripción");
            columnDescription.setCellValueFactory(
                    new PropertyValueFactory<>("description"));

            // agregar columnas a tabla
            tbl_reserve_categories.getColumns().add(columnId);
            tbl_reserve_categories.getColumns().add(columnDescription);

            // para obtener modelo de seleccion
            TableView.TableViewSelectionModel selectionModel =
                    tbl_reserve_categories.getSelectionModel();
            // se pueden seleccionar vairas
            selectionModel.setSelectionMode(
                    SelectionMode.MULTIPLE);

            // agregar cada categoría disponible
            for(Category currentCategory : availableCategories){
                tbl_reserve_categories.getItems().add(currentCategory);
            }

        } catch (Exception e){
            e.printStackTrace();
        }


        // --- INCIIALIZAR TABLA DE RESERVAS ACTUALES
        TableColumn<Reservation, String> columnId =
                new TableColumn<>("ID");
        columnId.setCellValueFactory(
                new PropertyValueFactory<>("id"));


        TableColumn<Reservation, String> columnDescription =
                new TableColumn<>("Actividad");
        columnDescription.setCellValueFactory(
                new PropertyValueFactory<>("description"));


        TableColumn<Reservation, LocalDateTime> columnStart =
                new TableColumn<>("Inicio");
        columnStart.setCellValueFactory(
                new PropertyValueFactory<>("startDate"));
        TableColumn<Reservation, LocalDateTime> columnEnd =
                new TableColumn<>("Fin");
        columnEnd.setCellValueFactory(
                new PropertyValueFactory<>("endDate"));


        tbl_reserve_current.getColumns().add(columnId);
        tbl_reserve_current.getColumns().add(columnDescription);
        tbl_reserve_current.getColumns().add(columnStart);
        tbl_reserve_current.getColumns().add(columnEnd);

        // agregar reservaciones disponibles para el usuario que inició sesion
        CurrentSession session = CurrentSession.getInstance();
        User currentUser = session.getLoggedUser();
        ArrayList<Reservation> userReservations = currentUser.getReservationList();
        ObservableList<Reservation> reservations = FXCollections.observableArrayList();

        // agregar cada una
        for(Reservation currentReservation : userReservations){
            tbl_reserve_current.getItems().add(currentReservation);
            System.out.print(currentReservation.getId());
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
                currentUser.removeReservation(selected);

                Utilities.showAlert("Confirmacion","Se ha eliminado la reserva", Alert.AlertType.INFORMATION);

                // todo: borrarlo del sistema en el xml
                // quitar fila
                tbl_reserve_current.getItems().remove(selected);

                // quitar seleccion
                selectionModel.clearSelection();
            }

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


                // TODO: MOVER A CAPA LOGICA
                if (selectedItems.isEmpty()) {
                    Utilities.showAlert("Error", "Debe de seleccionar al menos una categoria", Alert.AlertType.ERROR);
                } else {

                    String description = txt_reserve_activity.getText();

                    // obtener fecha
                    LocalDate date = dt.getValue();
                    // construir LocalDateTime para guardarlo
                    // TODO: ARREGLAR SPINNER Y CAMBIAR HORA POR DEFECTO
                    // TODO: ASIGNAR ID AUTOMATICO
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.atStartOfDay();

                    ReservationDTO r = new ReservationDTO("testy", description, start, end, );


                    }

                    // finalmente agregar reserva construida al usuario
                    // TODO: AGREGAR A XML
                    try {
                        System.out.println(r);
                        currentUser.addReservation(r);
                    } catch (InstanceAlreadyExistsException e) {
                        e.printStackTrace();
                    }

                    Utilities.showAlert("Confirmacion", "Se ha reservado con exito", Alert.AlertType.INFORMATION);
                    // quitar seleccion
                    selectionModel.clearSelection();

                    // agregar nueva reserva
                    tbl_reserve_current.getItems().add(r);
                }

            }
        });


        btn_reserve_ai.setOnAction(event -> {
            enviarMensaje();
        });


    }

    private void enviarMensaje(){
        String texto = txt_reserve_prompt.getText().trim(); // trim quita espacios en blanco o saltos de linea al final
        if(texto.isEmpty()) return; // no se puede contestar una pregunta vacia

        // tirar un hilo para que se pueda seguir haciendo cosas mientras gemini ejecuta otra tarea (en paralelo)
        Thread hiloGemini = new Thread(()->{
            // funcion vacia con "()" porque la aplicacion no es dueña de lo que es Gemini, el proceso no es mio fuera
            // del contexto de la aplicación, igual cuando se accede a la base de datos

            // poner try y catch porque no domino Gemini y puede caerse.
            try{
                GeminiService geminiService = new GeminiService();
                String respuesta = geminiService.enviarMensaje(texto); // la pregunta que el usuario hace
                // cruzar plataforma java con la de gemini
                Platform.runLater(()->{
                    // append a lo que responde la IA apenas termine de generar la respuesta
                    //txtHistorialConversacion.appendText("AVI responde: " + respuesta + "\n");
                });
            }catch(Exception e){
                Platform.runLater(()->{
                    // manejo de error
                });
            }
        });

        hiloGemini.setDaemon(true); // "Poseer" el flujo principal y cambiarlo al flujo anterior cuando termine
        hiloGemini.start(); // comenzarlo
    }
}
