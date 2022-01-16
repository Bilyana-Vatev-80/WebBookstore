package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.service.UserRoleService;
import bg.softuni.webbookstore.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/roles")
public class UserRoleController {

    private final UserRoleService userRoleService;
    private final UserService userService;

    public UserRoleController(UserRoleService userRoleService, UserService userService) {
        this.userRoleService = userRoleService;
        this.userService = userService;
    }


    @GetMapping("/assign")
    public String assign(Model model) {
        model.addAttribute("usernames", userService.findAllUsernames());
        return "assign-role";
    }

    @PostMapping("/assign")
    public String assignConfirm(@RequestParam String username,
                                @RequestParam String role) {

        userRoleService.assignUserRole(username, UserRoleEnum.valueOf(role.toUpperCase()));

        return "redirect:/books/all";
    }
}
