package resourcemanager.logic;

import resourcemanager.model.User;
import resourcemanager.structure.GlobalLists;

import java.util.regex.Pattern;

public class PasswordManager {
    private static final Pattern PASSWORD_POLICY = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$"); //Reglas de negocio REGEX
    public static final String PASSWORD_POLICY_MSG = "Debe tener al menos 8 caracteres, e incluir mayúsculas, minúsculas, números y símbolos (ej: !@#$%).";

    private  PasswordManager() {}

    public static Boolean verifyPhone(User user, String phoneNumber){
        if (user==null || phoneNumber==null) return false;

        String guardado = user.getPhoneNumber() == null ? "" : user.getPhoneNumber().trim();
        String digitado = phoneNumber.trim();
        return !guardado.isEmpty() && guardado.equalsIgnoreCase(digitado);
    }

    public static Boolean cumplePolitica(String password){
        return password != null && PASSWORD_POLICY.matcher(password).matches();
    }

    public static void updatePassword(String userID, String newPassword) throws Exception{
        GlobalLists.getInstance().updatePassword(userID,newPassword); //Actualiza la clave
        User memUser = GlobalLists.userList.findById(userID); //Obtiene el usuario guardado en memoria
        if (memUser!=null) {memUser.setPassword(newPassword);} //Si existe, actualiza clave
    }
}
