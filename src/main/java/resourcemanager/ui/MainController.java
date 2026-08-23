package resourcemanager.ui;
import javafx.event.ActionEvent; // para los movimientos de las pantallas (click, drag, drop...)
import javafx.fxml.FXML; // poder entender fxml
import javafx.fxml.FXMLLoader; // para poder moverse a otra pantalla
import javafx.scene.Node; // como en grafos de estructuras, es un árbol
import javafx.scene.Parent; // en algun momento se ocupa regresar al padre
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage; // necesitamos un escenario para correr
import resourcemanager.ui.Utilities;

// importar solo los elementos de la UI que se ocupa porque cualquier cosa lo que se importe
// se compila de todas formas incluiso aunque no se utiliza
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import resourcemanager.User;
import resourcemanager.structure.GlobalLists;
import resourcemanager.structure.UserList;

public class MainController {
    @FXML
    private void initialize() {
    }
}
