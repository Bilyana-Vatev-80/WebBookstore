package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;

public interface UserRoleService {

    void assignUserRole(String username, UserRoleEnum roleEnum);
}
