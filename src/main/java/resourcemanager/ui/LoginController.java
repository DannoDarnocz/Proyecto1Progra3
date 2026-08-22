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

// los label no cambian en esta pantalla asi que no hace falta importarlo, no genera eventos


public class LoginController {
    @FXML Button btn_login;
    @FXML TextField txt_user;
    @FXML
    PasswordField pwd_password;

    private User attemptLogin(String userId, String password){
        UserList users = GlobalLists.userList;
        User foundUser = users.findById(userId);
        // si no existe o la contraseña no coincide retornar null
        if(foundUser==null || !foundUser.getPassword().equals(password)) return null;
        // poner flag de que es admin de una vez
        adminLoggedIn = foundUser.getIsAdmin();
        return foundUser;
    }

    public static boolean adminLoggedIn = false;

    private void showAlert(String title, String msg, Alert.AlertType type){
        // configurar alerta
        Alert loadingAlert = new Alert(type);
        loadingAlert.setHeaderText(title);
        loadingAlert.setContentText(msg);

        // mostrarla
        loadingAlert.show();
    }

    @FXML
    private void initialize(){
        // verificar inicio de sesión al darle al botón
        btn_login.setOnAction(event -> {
            String userInput = txt_user.getText().trim(); // eliminar espacios en blanco
            String passwordInput = pwd_password.getText().trim();

            // todo: implementar verificacion de usuarios
            User foundUser = attemptLogin(userInput, passwordInput);
            if(foundUser != null){
                showAlert("Inicio de sesion correcto","Bienvenido", Alert.AlertType.CONFIRMATION);
                // todo: arreglar esta porquería
                Utilities.switchScreen(event, "/resourcemanager/ui/main.fxml");
            }
            else{
                showAlert("Error","Usuario o contraseña incorrecto", Alert.AlertType.ERROR);
            }
        });
    }
}
