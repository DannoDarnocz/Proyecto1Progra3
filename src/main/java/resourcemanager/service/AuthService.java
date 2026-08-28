package resourcemanager.service;

import resourcemanager.logic.AuthLogic;
import resourcemanager.model.User;
import resourcemanager.model.dto.UserLoginDTO;
import resourcemanager.structure.CurrentSession;

public class AuthService {
    public static User authenticate(UserLoginDTO input) throws Exception{
        return AuthLogic.authenticate(input);
    }
}
