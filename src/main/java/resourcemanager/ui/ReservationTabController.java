package resourcemanager.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import resourcemanager.model.Reservation;
import resourcemanager.structure.GlobalLists;

import java.time.LocalDate;
import java.util.ArrayList;

public class ReservationTabController {
    @FXML
    private TextArea txt_reserve_prompt;
    @FXML
    private Button btn_reserve_print;
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
        btn_reserve_confirm.setOnAction(event -> {
            /*try{
                String activity = txt_reserve_activity.getText();
                LocalDate date = dt.getValue();

                Reservation newReservation = new Reservation();
                ArrayList<Reservation> reservationList = GlobalLists.reservationList;
                reservationList.add();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        });
    }
}
