package resourcemanager.logic;

import resourcemanager.data.SaveToXML;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;
import resourcemanager.structure.CurrentSession;

import java.util.regex.Pattern;

public class AuthLogic {
    public static final String PASSWORD_POLICY_MSG = "Debe tener al menos 8 caracteres, e incluir mayúsculas, minúsculas, números y símbolos (ej: !@#$%).";
    private static final Pattern PASSWORD_POLICY = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$"); //Reglas de negocio REGEX

    public static User authenticate(UserLoginDTO input) throws Exception {
        String id = input.getUser();
        // obtener usuario solo enviando el id en vez del DTO (sino el codigo no seria reutilizable bajo otro contexto)
        // aqui no hay try si se lanza excepcion porque simplemente se cancela la operación y en el controlador se hace catch
        User foundUser = UserLogic.findUserById(id);

        // si no existe, hacerlo saber
        if(foundUser == null) return null;

        // comparar dato recibido del usuario con la contraseña almacenada en el archivo
        if (foundUser.getPassword().equals(input.getPassword())){
            // corresponde, asignar a la sesión actual
            CurrentSession currentSession = CurrentSession.getInstance();
            currentSession.setLoggedUser(foundUser);
            // devolver usuario encontrado
            return foundUser;
        } else {
            // es mejor no hacerle saber al usuario si tuvo la contraseña incorrecta o el usuario incorrecto
            return null;
        }
    }

    public static Boolean verifyPhone(User user, String phoneNumber){
        if (user==null || phoneNumber==null) return false;

        String guardado = user.getPhoneNumber() == null ? "" : user.getPhoneNumber().trim();
        String digitado = phoneNumber.trim();
        return !guardado.isEmpty() && guardado.equalsIgnoreCase(digitado);
    }


    public static Boolean satisfiesPolicy(String password){
        return password != null && PASSWORD_POLICY.matcher(password).matches();
    }

    public static void updatePassword(UserLoginDTO newUserLogin) throws Exception{
        String id = newUserLogin.getUser();
        String newPassword = newUserLogin.getPassword();

        User memUser = UserLogic.findUserById(id); //Obtiene el usuario guardado en memoria
        if (memUser!=null) { //Si existe, actualiza clave de forma lógica y luego envia al XML
            memUser.setPassword(newPassword);
            SaveToXML.updateUser(memUser);
        }
    }
}
