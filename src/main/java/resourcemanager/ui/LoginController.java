package resourcemanager.ui;
import javafx.event.ActionEvent; // para los movimientos de las pantallas (click, drag, drop...)
import javafx.fxml.FXML; // poder entender fxml
import javafx.fxml.FXMLLoader; // para poder moverse a otra pantalla
import javafx.scene.Node; // como en grafos de estructuras, es un árbol
import javafx.scene.Parent; // en algun momento se ocupa regresar al padre
import javafx.stage.Stage; // necesitamos un escenario para correr

// importar solo los elementos de la UI que se ocupa porque cualquier cosa lo que se importe
// se compila de todas formas incluiso aunque no se utiliza
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

// los label no cambian en esta pantalla asi que no hace falta importarlo, no genera eventos

public class LoginController {
    @FXML Button btn_login;
    @FXML TextField txt_user;
    @FXML TextField txt_password;

    @FXML
    private void initialize(){
        // verificar inicio de sesión al darle al botón
        btn_login.setOnAction(event -> {
            String userInput = txt_user.getText().trim(); // eliminar espacios en blanco
            String passwordInput = txt_password.getText().trim();

            // todo: implementar verificacion de usuarios
            /*
                User foundUser = users.search(userInput)
                if(foundUser != null && foundUser.verifyPassword(passwordInput)){
                    // inicio de sesión correcto
                }
                else{
                    // inicio de sesión incorrecto, no pasar de ahi y mostrar mensaje de error
                }
            */
        });
    }
}
