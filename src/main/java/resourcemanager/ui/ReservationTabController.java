package resourcemanager.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import resourcemanager.service.GeminiService;

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
        btn_reserve_ai.setOnAction(event -> {
            enviarMensaje();
        });

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
