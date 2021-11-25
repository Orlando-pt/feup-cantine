package pt.feup.les.feupfood.util;

import pt.feup.les.feupfood.dto.RegisterUserDto;
import pt.feup.les.feupfood.dto.RegisterUserResponseDto;
import pt.feup.les.feupfood.dto.UserDto;
import pt.feup.les.feupfood.model.DAOUser;

public class UserParser {
    
    public static UserDto registerUsertoUserDto(RegisterUserDto user, String role) {
        var daoUser = new UserDto();
        daoUser.setEmail(user.getEmail());
        daoUser.setFullName(user.getFullName());
        daoUser.setPassword(user.getPassword());
        daoUser.setTerms(user.getTerms());
        daoUser.setRole(role);

        return daoUser;
    }
    
    public static DAOUser registerUsertoDaoUser(UserDto user) {
        var daoUser = new DAOUser();
        daoUser.setEmail(user.getEmail());
        daoUser.setFullName(user.getFullName());
        daoUser.setTerms(user.getTerms());
        daoUser.setRole(user.getRole());

        return daoUser;
    }

    public static RegisterUserResponseDto daoUserToRegisterUserResponse(DAOUser daoUser) {
        var user = new RegisterUserResponseDto();
        user.setEmail(daoUser.getEmail());
        user.setFullName(daoUser.getFullName());
        user.setRole(daoUser.getRole());

        return user;
    }
}
