package resourcemanager.service;

import resourcemanager.logic.AuthLogic;
import resourcemanager.logic.UserLogic;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;
import resourcemanager.structure.CurrentSession;

public class AuthService {
    public static User authenticate(UserLoginDTO input) throws Exception{
        return AuthLogic.authenticate(input);
    }
    public static boolean verifyPhone(User user, String phoneNumber) throws Exception{
        return AuthLogic.verifyPhone(user,phoneNumber);
    }
    public static void updatePassword(UserLoginDTO newUserLogin) throws Exception{
        AuthLogic.updatePassword(newUserLogin);
    }
    public static Boolean satisfiesPolicy(String password) {
        return AuthLogic.satisfiesPolicy(password);
    }
    public static String policyPassword(){
        return AuthLogic.PASSWORD_POLICY_MSG;
    }
}
