package resourcemanager.logic;

import resourcemanager.filehandler.LoadXML;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;

public class AuthLogic {
    public static User authenticate(UserLoginDTO input) throws Exception {
        String id = input.getUser();
        // obtener usuario solo enviando el id en vez del DTO (sino el codigo no seria reutilizable bajo otro contexto)
        // aqui no hay try si se lanza excepcion porque simplemente se cancela la operación y en el controlador se hace catch
        User foundUser = LoadXML.findUserById(id);

        // si no existe, hacerlo saber
        if(foundUser == null) return null;

        // comparar dato recibido del usuario con la contraseña almacenada en el archivo
        if (foundUser.getPassword().equals(input.getPassword())){
            // corresponde, devolver usuario encontrado
            return foundUser;
        } else {
            // es mejor no hacerle saber al usuario si tuvo la contraseña incorrecta o el usuario incorrecto
            return null;
        }
    }
}
