package resourcemanager.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import resourcemanager.model.User;
import javafx.print.PrinterJob;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.lang.reflect.Field;
import java.util.List;

import java.util.Objects;

public class Utilities {

    private Utilities() {}

    public static void cambiarPantalla(ActionEvent evento, String archivoFxml, double width, double height, boolean resizable){
        try{
            // cargar archivo pasado por parametro
            Parent raiz = FXMLLoader.load(Objects.requireNonNull(Utilities.class.getResource(archivoFxml)));
            // cambiar el escenario a la siguiente ventana
            Stage stage=(Stage)((Node)evento.getSource()).getScene().getWindow();

            stage.setMinWidth(0);
            stage.setMinHeight(0);

            stage.getScene().setRoot(raiz); // devolver a la raiz al cerrarla
            stage.setResizable(resizable);

            stage.setWidth(width);
            stage.setHeight(height);
            stage.setMinWidth(width);
            stage.setMinHeight(height);
            stage.centerOnScreen();

            stage.setTitle("Sistema de Reservas");
        } catch (Exception e){
            e.printStackTrace(); // imprimir en consola el errorr
        }
    }

    public static Alert showAlert(String title, String msg, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setHeaderText(title);
        alert.setContentText(msg);

        Stage owner = resourcemanager.structure.AppContext.getPrimaryStage();
        if (owner != null) {
            alert.initOwner(owner);
        }
        // si es de confirmacion entonces importa la respuesta, hay que mostrarla desde afuera y setear su evento
        if(type!= Alert.AlertType.CONFIRMATION){
            alert.show();
        }
        return alert;
    }

    public static void showError(Exception e) {
        String mensaje = e.getMessage() != null ? e.getMessage() : "Ha ocurrido un error inesperado";
        showAlert("Error", mensaje, Alert.AlertType.ERROR);
    }


}
