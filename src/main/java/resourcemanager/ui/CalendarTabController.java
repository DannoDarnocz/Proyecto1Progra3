package resourcemanager.ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML; // poder entender fxml
import javafx.scene.control.*;
import resourcemanager.model.Category;
import resourcemanager.model.Reservation;
import resourcemanager.model.Resource;
import resourcemanager.model.User;
import resourcemanager.model.tables.ResouceInfoRow;
import resourcemanager.model.tables.ResourceInfoCell;
import resourcemanager.service.CategoryService;
import resourcemanager.service.ReservationService;
import resourcemanager.service.ResourceService;
import resourcemanager.service.UserService;
import resourcemanager.structure.CurrentSession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CalendarTabController {
    @FXML
    private Button btn_print;
    @FXML
    private Button btn_load;
    @FXML
    private DatePicker dt_date;
    @FXML
    private ChoiceBox<Category> cb_category;
    @FXML
    private TableView tbl_matrix;

    private void reload(){
        try{
            // obtener categorias como arraylist y luego convertirlas a observablelist para meterlas en el choicebox
            ArrayList<Category> categories = CategoryService.getAllCategories();
            ObservableList<Category> categoriesObservable = FXCollections.observableArrayList(categories);
            cb_category.setItems(categoriesObservable);
        } catch (Exception e) {

            Utilities.showAlert("Error","No se han podido cargar las categorias " + e.getMessage(), Alert.AlertType.ERROR);

        }
    }



    @FXML
    private void initialize() {
        // cargar categorias
        reload();



        btn_load.setOnAction(event -> {
            // obtener categoria y date
            LocalDate date = dt_date.getValue();
            Category category = (Category) cb_category.getValue();

            if(date==null || category == null){
                Utilities.showAlert("Error","Debe de seleccionar fecha y categoria", Alert.AlertType.ERROR);
            }
            else{
                ArrayList<Reservation> matchingReservations = null;
                try{
                    // primero obtener reservas que coinciden con la categoria
                    matchingReservations = ResourceService.findReservationsForCategory(category.getId());


                    // luego filtrarlas por fecha (la fecha es la misma)
                    matchingReservations = ReservationService.filterByDate(matchingReservations,date,date);

                    if(matchingReservations==null){
                        Utilities.showAlert("Informacion","No hay recursos asignados en la categoria y fecha", Alert.AlertType.INFORMATION);
                    }
                    else{
                        // un map tiene cada elemento asociado a un id
                        Map<LocalTime, Map<String, ResourceInfoCell>> grid = new HashMap<>();

                        // obtener pool de recursos de las reservas
                        try{
                            ArrayList<Resource> matchingResources = ReservationService.extractResources(matchingReservations);

                            for (Reservation res : matchingReservations) {
                                User user = UserService.findUserForReservation(res); // called once per reservation
                                if(user!=null){

                                    ResourceInfoCell info = new ResourceInfoCell();
                                    info.userName = user.getName();
                                    info.description = res.getDescription();

                                    // ir recorriendo cada hora, de saltos de 1 por intervalo, obteniendo la fila entera para los recursos que en esa hora
                                    // tengan una reserva asociada
                                    for (LocalTime t = res.getStartDate().toLocalTime(); t.isBefore(res.getEndDate().toLocalTime()); t = t.plusHours(1)) {
                                        for (String resourceId : res.getResourceIdList()) {
                                            // crea un mapa vacío para la "clave" actual si no existe y añade el resourceId y la informacion del recurso
                                            grid.computeIfAbsent(t, k -> new HashMap<>()).put(resourceId, info);
                                        }
                                    }
                                }
                                else{
                                    Utilities.showAlert("Error","No se ha podido encontrar un usuario asociado a la reserva. ", Alert.AlertType.ERROR);
                                }

                            }

                            ObservableList<ResouceInfoRow> rows = FXCollections.observableArrayList();
                            for (LocalTime t = LocalTime.of(0, 0); t.isBefore(LocalTime.of(23, 0)); t = t.plusHours(1)) {
                                rows.add(new ResouceInfoRow(t, grid.getOrDefault(t, Collections.emptyMap())));
                            }
                            tbl_matrix.setItems(rows);

                            // columna para mostrar la hora
                            TableColumn<ResouceInfoRow, String> timeCol = new TableColumn<>("Hora");
                            timeCol.setCellValueFactory(cellData -> {
                                // obtener el valor de la hora de fila actual, ponerle menos y leugo sumarle una hora a la misma
                                LocalTime start = cellData.getValue().getHour();
                                String text = start + " - " + start.plusHours(1);
                                return new ReadOnlyStringWrapper(text);
                            });
                            tbl_matrix.getColumns().add(timeCol);

                            for (Resource resource : matchingResources) {
                                TableColumn<ResouceInfoRow, String> col = new TableColumn<>(resource.getDescription());
                                col.setCellValueFactory(cellData -> {
                                    ResourceInfoCell info = cellData.getValue().getInfo(resource.getId());
                                    String text = (info == null) ? "" : info.userName + "\n" + info.description;
                                    return new ReadOnlyStringWrapper(text);
                                });
                                tbl_matrix.getColumns().add(col);
                            }
                        } catch (Exception e) {
                            Utilities.showAlert("Error","ha ocurrido un error al buscar recursos asociados a la fecha y categoria", Alert.AlertType.ERROR);
                        }


                    }
                } catch (Exception e) {
                    Utilities.showAlert("Error","No se ha podido obtener la lista de reservas para la categoria seleccionada. " + e.getMessage(), Alert.AlertType.ERROR);
                }


            }
        });
    }
}
