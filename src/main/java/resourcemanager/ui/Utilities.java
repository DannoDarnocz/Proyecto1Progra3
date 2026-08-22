package resourcemanager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Utilities {
    public static void switchScreen(ActionEvent evento, String archivoFxml){
        try{
            // cargar archivo pasado por parametro
            Parent raiz = FXMLLoader.load(Utilities.class.getClass().getResource(archivoFxml));
            // cambiar el escenario a la siguiente ventana
            Stage stage=(Stage)((Node)evento.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz); // devolver a la raiz al cerrarla
            stage.sizeToScene();
        } catch (Exception e){
            e.printStackTrace(); // imprimir en consola el errorr
        }
    }
}
