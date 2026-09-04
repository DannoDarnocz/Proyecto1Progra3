package resourcemanager.service;

import resourcemanager.logic.AuthLogic;
import resourcemanager.logic.UserLogic;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;
import resourcemanager.structure.CurrentSession;

public class AuthService {
    private AuthLogic authLogic = new AuthLogic();

    public User authenticate(UserLoginDTO input) throws Exception{
        return authLogic.authenticate(input);
    }
    public boolean verifyPhone(User user, String phoneNumber) throws Exception{
        return authLogic.verifyPhone(user,phoneNumber);
    }
    public void updatePassword(UserLoginDTO newUserLogin) throws Exception{
        authLogic.updatePassword(newUserLogin);
    }
    public Boolean satisfiesPolicy(String password) {
        return authLogic.satisfiesPolicy(password);
    }
    public String policyPassword(){
        return AuthLogic.PASSWORD_POLICY_MSG;
    }
}
