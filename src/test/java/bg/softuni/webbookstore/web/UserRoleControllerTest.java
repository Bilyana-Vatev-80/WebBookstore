package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.entity.UserRoleEntity;
import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.repository.UserRepository;
import bg.softuni.webbookstore.repository.UserRoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserRoleControllerTest {

    private static final String USERNAME_TO_ASSIGN_ROLE = "testUser";
    private static final String ROLE_NAME = "Admin";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private UserRoleEntity adminRole;
    private UserRoleEntity userRole;
    private UserEntity testAdmin;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        userRoleRepository.deleteAll();
        userRepository.deleteAll();

        adminRole = initAdminRole();
        userRole = initUserRole();
        testAdmin = initTestAdmin();
        testUser = initTestUser();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void test_GetAssignRoleForm_OpensForm() throws Exception {
        mockMvc
                .perform(get("/roles/assign"))
                .andExpect(status().isOk())
                .andExpect(view().name("assign-role"))
                .andExpect(model().attributeExists("usernames"));
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void test_AssignNewRole_AddsRoleToUserRoles() throws Exception {
        mockMvc
                .perform(post("/roles/assign")
                        .param("username", USERNAME_TO_ASSIGN_ROLE)
                        .param("role", ROLE_NAME)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/all"));

        List<String> roles = userRepository.findByUsername(USERNAME_TO_ASSIGN_ROLE)
                .get()
                .getRoles()
                .stream()
                .map(userRoleEntity -> userRoleEntity.getRole().name())
                .collect(Collectors.toList());
        assertTrue(roles.contains("ADMIN"));
    }

    private UserRoleEntity initAdminRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN));
    }

    private UserRoleEntity initUserRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.USER));
    }

    private UserEntity initTestAdmin() {
        return userRepository.save(new UserEntity()
                .setFirstName("Test")
                .setLastName("Testov")
                .setUsername("testadmin")
                .setEmail("admin@admin.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(adminRole)));
    }

    private UserEntity initTestUser() {
        return userRepository.save(new UserEntity()
                .setFirstName("Test")
                .setLastName("Testov")
                .setUsername("testUser")
                .setEmail("user@user.bg")
                .setAddress("Sofia")
                .setPassword("12345")
                .setRoles(List.of(userRole)));
    }
}