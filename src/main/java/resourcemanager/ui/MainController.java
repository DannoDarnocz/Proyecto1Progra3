package resourcemanager.ui;

import javafx.fxml.FXML; // poder entender fxml
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import resourcemanager.model.User;
import resourcemanager.structure.CurrentSession;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import resourcemanager.ui.ResourcesTabController;

// importar solo los elementos de la UI que se ocupa porque cualquier cosa lo que se importe
// se compila de todas formas incluso aunque no se utiliza


public class MainController {
    @FXML
    private Button btn_logout;
    @FXML
    private Label lbl_logged_user;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab tab_recursos;
    @FXML
    private ResourcesTabController resourcesTabIncludeController;

    @FXML
    private void initialize() {
        User usuarioActual = CurrentSession.getInstance().getLoggedUser();
        if (usuarioActual != null) {
            lbl_logged_user.setText("Usuario: " + usuarioActual.getId() + (Boolean.TRUE.equals(usuarioActual.getIsAdmin()) ? " (Admin)" : ""));
        }
        btn_logout.setOnAction(event -> {
            Alert confirmacion = Utilities.showAlert("Cerrar sesión","¿Desea cerrar la sesión",Alert.AlertType.CONFIRMATION);
            confirmacion.showAndWait().ifPresent(respuesta ->{
                if (respuesta == ButtonType.OK){
                    CurrentSession.getInstance().logout();
                    Utilities.cambiarPantalla(event,"/resourcemanager/ui/login.fxml",600,400,false);
                }
            });
        });
        if (mainTabPane != null && tab_recursos != null && resourcesTabIncludeController != null) {
            mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab == tab_recursos) {
                    resourcesTabIncludeController.refreshCategoryChoices();
                }
            });
        }
    }
}
