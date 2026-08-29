package resourcemanager.ui;
import com.fasterxml.jackson.databind.JsonMappingException;
import javafx.fxml.FXML; // poder entender fxml
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

// importar solo los elementos de la UI que se ocupa porque cualquier cosa lo que se importe
// se compila de todas formas incluiso aunque no se utiliza
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import resourcemanager.logic.AuthLogic;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;
import resourcemanager.service.AuthService;

import java.io.FileNotFoundException;
import java.util.Optional;

// los label no cambian en esta pantalla asi que no hace falta importarlo, no genera eventos


public class LoginController {

    @FXML Button btn_login;
    @FXML Button btn_change_pwd;
    @FXML TextField txt_user;
    @FXML PasswordField pwd_password;

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
                Utilities.showAlert("Error","Debe llenar ambos campos", Alert.AlertType.ERROR);
            }
            else{
                // datos correctos, construir DTO
                UserLoginDTO loginDTO = new UserLoginDTO(userInput, passwordInput);

                // enviarlo a serivicio que envia a capa logica y recibir el usuario encontrado (si hay) y sale bien
                try{
                    User foundUser = AuthService.authenticate(loginDTO);

                    // si se encontró entonces pasar a la pantalla principal
                    if(foundUser != null){
                        Utilities.cambiarPantalla(event, "/resourcemanager/ui/main.fxml",750,700,true);
                    }
                    else{
                        Utilities.showAlert("Error","Usuario o contraseña incorrecto", Alert.AlertType.ERROR);
                    }
                } catch (JsonMappingException e){
                    Utilities.showAlert("Error","La base de datos posee un archivo formateado de forma incorrecta", Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
                catch (FileNotFoundException e) {
                    Utilities.showAlert("Error","No se ha encontrado la base de datos de usuarios", Alert.AlertType.ERROR);
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        btn_change_pwd.setOnAction(event -> cambioClave());
    }

    private void cambioClave(){
        //Reutiliza verificación
        String userInput = txt_user.getText().trim();
        String currentPwdInput = pwd_password.getText().trim();

        if(userInput.isEmpty() || currentPwdInput.isEmpty()){
            Utilities.showAlert("Error","Debe llenar ambos campos para poder cambiar la clave", Alert.AlertType.ERROR);
            return;
        }

        try{
            UserLoginDTO loginDTO = new UserLoginDTO(userInput, currentPwdInput);
            User foundUser = AuthService.authenticate(loginDTO);

            // si es null es porque no se encontró o la contraseña es incorrecta (por seguridad es mejor no concoer ninguno)
            if(foundUser == null){
                Utilities.showAlert("Error","Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
                return;
            }

            Optional<String> telefonoIngresado = pedirTelefono();
            if(telefonoIngresado.isEmpty()) return; // canceló
            if(!AuthLogic.verifyPhone(foundUser,telefonoIngresado.get())){
                Utilities.showAlert("Error","El teléfono ingresado no coincide con el registrado", Alert.AlertType.ERROR);
                return;
            }

            //Optional es valor que puede estar presente o no, mejor solución a un null para verificación
            Optional<String> nuevaPassword = pedirNuevaContrasena();
            if(nuevaPassword.isEmpty()) return; // canceló o no coincidían

            // crear dto para la nueva contraseña (el user queda igual obviamente)
            UserLoginDTO newUserLogin = new UserLoginDTO(userInput,nuevaPassword.get());

            AuthLogic.updatePassword(newUserLogin);

            Utilities.showAlert("Exito","Contraseña actualizada correctamente", Alert.AlertType.INFORMATION);
            pwd_password.clear();

        } catch (FileNotFoundException e) {
            Utilities.showAlert("Error","No se ha encontrado la base de datos de usuarios", Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            Utilities.showAlert("Error","No se pudo actualizar la contraseña", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private Optional<String> pedirTelefono(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Verificación adicional");
        dialog.setHeaderText("Para continuar, confirmá el teléfono registrado en tu cuenta");
        dialog.setContentText("Teléfono:");

        return dialog.showAndWait().map(String::trim);
    }

    private Optional<String> pedirNuevaContrasena(){
        //Se crea un pop up de dialogo
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Cambiar contraseña");
        dialog.setHeaderText("Ingresá tu nueva contraseña");

        //Se crean los botones porque por defecto no los tiene
        ButtonType confirmarBtn = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmarBtn, ButtonType.CANCEL);

        //Se crean los espacios de claves
        PasswordField nuevaPwd = new PasswordField();
        nuevaPwd.setPromptText("Nueva contraseña");
        PasswordField confirmarPwd = new PasswordField();
        confirmarPwd.setPromptText("Confirmar contraseña");

        Label requisitosLabel = new Label(AuthLogic.PASSWORD_POLICY_MSG);
        requisitosLabel.setWrapText(true);
        requisitosLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666666;");

        //Se ajustan con un GridPane los Label
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nueva:"), nuevaPwd);
        grid.addRow(1, new Label("Confirmar:"), confirmarPwd);
        grid.add(requisitosLabel, 0, 2, 2, 1);
        dialog.getDialogPane().setContent(grid);

        //Retorna el resultado de confirmar
        dialog.setResultConverter(boton -> {
            if(boton == confirmarBtn){
                String nueva = nuevaPwd.getText();
                String confirmar = confirmarPwd.getText();

                if(!nueva.equals(confirmar)){
                    Utilities.showAlert("Error","Las contraseñas no coinciden", Alert.AlertType.ERROR);
                    return null;
                }
                if(!AuthLogic.satisfiesPolicy(nueva)){
                    Utilities.showAlert("Contraseña inválida", AuthLogic.PASSWORD_POLICY_MSG, Alert.AlertType.ERROR);
                    return null;
                }

                return nueva;
            }
            return null;
        });

        return dialog.showAndWait();
    }

}
