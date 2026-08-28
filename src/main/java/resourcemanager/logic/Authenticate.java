package resourcemanager.logic;

import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;

public class Authenticate {
    public static User authenticate(UserLoginDTO input) throws Exception {
        String id = input.getUser();
        // obtener usuario solo enviando el id en vez del DTO (sino el codigo no seria reutilizable bajo otro contexto)
        User foundUser = UserLogic.findUserById(id);

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
