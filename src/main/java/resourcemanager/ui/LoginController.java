package resourcemanager.ui;
import javafx.event.ActionEvent; // para los movimientos de las pantallas (click, drag, drop...)
import javafx.fxml.FXML; // poder entender fxml
import javafx.fxml.FXMLLoader; // para poder moverse a otra pantalla
import javafx.scene.Node; // como en grafos de estructuras, es un árbol
import javafx.scene.Parent; // en algun momento se ocupa regresar al padre
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage; // necesitamos un escenario para correr

// importar solo los elementos de la UI que se ocupa porque cualquier cosa lo que se importe
// se compila de todas formas incluiso aunque no se utiliza
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import resourcemanager.filehandler.LoadXML;
import resourcemanager.logic.Authenticate;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLogin;
import resourcemanager.structure.CurrentSession;
import resourcemanager.structure.GlobalLists;
import resourcemanager.structure.UserList;

import java.io.FileNotFoundException;

// los label no cambian en esta pantalla asi que no hace falta importarlo, no genera eventos


public class LoginController {

    private static final double FIXED_WIDTH = 600;
    private static final double FIXED_HEIGHT = 700;

    @FXML Button btn_login;
    @FXML Button btn_change_pwd;
    @FXML TextField txt_user;
    @FXML
    PasswordField pwd_password;

    /// Esto ya no aplica
    /*
    private User attemptLogin(String userId, String password){
        UserList users = GlobalLists.userList;
        User foundUser = users.findById(userId);
        // si no existe o la contraseña no coincide retornar null
        if(foundUser==null || !foundUser.getPassword().equals(password)) return null;
        // poner flag de que es admin de una vez
        adminLoggedIn = foundUser.getIsAdmin();
        return foundUser;
    }*/


    @FXML
    private void initialize(){
        // verificar inicio de sesión al darle al botón
        btn_login.setOnAction(event -> {
            String userInput = txt_user.getText().trim();
            String passwordInput = pwd_password.getText().trim();

            if(userInput.isEmpty()||passwordInput.isEmpty()){
                showAlert("Error","Debe llenar ambos campos", Alert.AlertType.ERROR);
            }
            else{
                // datos correctos, construir DTO
                UserLogin loginDTO = new UserLogin(userInput, passwordInput);

                // enviarlo a capa logica y recibir el usuario encontrado (si hay) y todo sale bien
                try{
                    User foundUser = Authenticate.authenticate(loginDTO);

                    if(foundUser != null){
                        // almacenar usuario en clase singleton para que las otras pantallas lo conozcan
                        CurrentSession currentSession = CurrentSession.getInstance();
                        currentSession.setLoggedUser(foundUser);

                        cambiarPantalla(event, "/resourcemanager/ui/main.fxml");
                    }
                    else{
                        showAlert("Error","Usuario o contraseña incorrecto", Alert.AlertType.ERROR);
                    }
                }catch (FileNotFoundException e) {
                    showAlert("Error","No se ha encontrado la base de datos de usuarios", Alert.AlertType.ERROR);
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    // todo: mover esto a otra clase
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
    private void showAlert(String title, String msg, Alert.AlertType type){
        // configurar alerta
        Alert loadingAlert = new Alert(type);
        loadingAlert.setHeaderText(title);
        loadingAlert.setContentText(msg);

        // mostrarla
        loadingAlert.show();
    }

}
