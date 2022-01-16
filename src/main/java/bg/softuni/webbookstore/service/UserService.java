package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.service.UserLoginServiceModel;
import bg.softuni.webbookstore.model.service.UserRegisterServiceModel;
import bg.softuni.webbookstore.model.view.UserViewModel;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void register(UserRegisterServiceModel userRegisterServiceModel);

    void login(UserLoginServiceModel loginServiceModel);

    boolean userNameExists(String username);

    Optional<UserViewModel> findByUsername(String username);

    List<String> findAllUsernames();

    boolean isAdmin(String username);
}
