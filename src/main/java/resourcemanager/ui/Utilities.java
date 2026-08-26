package resourcemanager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class Utilities {

    private Utilities() {}

    public static void cambiarPantalla(ActionEvent evento, String archivoFxml, double width, double height, boolean resizable){
        try{
            // cargar archivo pasado por parametro
            Parent raiz = FXMLLoader.load(Utilities.class.getResource(archivoFxml));
            // cambiar el escenario a la siguiente ventana
            Stage stage=(Stage)((Node)evento.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz); // devolver a la raiz al cerrarla
            stage.setResizable(resizable);

            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMinWidth(width);
            stage.setMinHeight(height);
        } catch (Exception e){
            e.printStackTrace(); // imprimir en consola el errorr
        }
    }

    public static void showAlert(String title, String msg, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.show();
    }

}
