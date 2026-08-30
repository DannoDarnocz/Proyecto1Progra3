package resourcemanager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import resourcemanager.model.User;

import java.util.Objects;

public class Utilities {

    private Utilities() {}

    public static void cambiarPantalla(ActionEvent evento, String archivoFxml, double width, double height, boolean resizable){
        try{
            // cargar archivo pasado por parametro
            Parent raiz = FXMLLoader.load(Objects.requireNonNull(Utilities.class.getResource(archivoFxml)));
            // cambiar el escenario a la siguiente ventana
            Stage stage=(Stage)((Node)evento.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz); // devolver a la raiz al cerrarla
            stage.setResizable(resizable);

            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMinWidth(width);
            stage.setMinHeight(height);
            stage.centerOnScreen();

            updateTitle(stage);
        } catch (Exception e){
            e.printStackTrace(); // imprimir en consola el errorr
        }
    }

    public static Alert showAlert(String title, String msg, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        // si es de confirmacion entonces importa la respuesta, hay que mostrarla desde afuera y setear su evento
        if(type!= Alert.AlertType.CONFIRMATION){
            alert.show();
        }
        return alert;
    }

    private static void updateTitle(Stage stage){
        resourcemanager.model.User loggedUser = resourcemanager.structure.CurrentSession.getInstance().getLoggedUser();
        if (loggedUser != null) {stage.setTitle("Sistema de Reservas - " + loggedUser.getId());}
        else { stage.setTitle("Sistema de Reservas");}
    }

}
