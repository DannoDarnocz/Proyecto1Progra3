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

    private static final double FIXED_WIDTH = 600;
    private static final double FIXED_HEIGHT = 700;

    @FXML Button btn_login;
    @FXML Button btn_change_pwd;
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
            String userInput = txt_user.getText().trim();
            String passwordInput = pwd_password.getText().trim();

            // todo: implementar verificacion de usuarios
            User foundUser = attemptLogin(userInput, passwordInput);
            if(foundUser != null){
                showAlert("Inicio de sesion correcto","Bienvenido", Alert.AlertType.CONFIRMATION);
                // todo: arreglar esta porquería
                cambiarPantalla(event, "/resourcemanager/ui/main.fxml");
            }
            else{
                showAlert("Error","Usuario o contraseña incorrecto", Alert.AlertType.ERROR);
            }
        });
    }

    private void cambiarPantalla(ActionEvent evento, String archivoFxml){
        try{
            // cargar archivo pasado por parametro
            Parent raiz = FXMLLoader.load(getClass().getResource(archivoFxml));
            // cambiar el escenario a la siguiente ventana
            Stage stage=(Stage)((Node)evento.getSource()).getScene().getWindow();
            stage.getScene().setRoot(raiz); // devolver a la raiz al cerrarla
            stage.setResizable(true);
            stage.setWidth(FIXED_WIDTH);
            stage.setHeight(FIXED_HEIGHT);
            stage.setMinWidth(FIXED_WIDTH);
            stage.setMinHeight(FIXED_HEIGHT);
        } catch (Exception e){
            e.printStackTrace(); // imprimir en consola el errorr
        }
    }
}
