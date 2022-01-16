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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String TEST_USERNAME = "testUser";

    private UserRoleEntity userRole;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRoleRepository.deleteAll();

        userRole = initUserRole();
        initTestUser();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
    }

    @Test
    void test_GetRegisterForm_OpensRegisterForm() throws Exception {
        mockMvc
                .perform(get("/users/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void test_GetLoginForm_OpensLoginForm() throws Exception {
        mockMvc
                .perform(get("/users/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void test_PostRegister_CreatesNewUser() throws Exception {
        mockMvc
                .perform(post("/users/register")
                        .param("firstName", "Test")
                        .param("lastName", "Testov")
                        .param("username", TEST_USERNAME)
                        .param("email", "test@test.bg")
                        .param("address", "Sofia")
                        .param("password", "12345")
                        .param("confirmPassword", "12345")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/login"));

        assertEquals(2, userRepository.count());

        Optional<UserEntity> newlyCreatedUserOpt = userRepository.findByUsername(TEST_USERNAME);
        assertTrue(newlyCreatedUserOpt.isPresent());

        UserEntity newlyCreatedUser = newlyCreatedUserOpt.get();
        assertEquals("Test", newlyCreatedUser.getFirstName());
        assertEquals("Testov", newlyCreatedUser.getLastName());
        assertEquals("test@test.bg", newlyCreatedUser.getEmail());
        assertEquals("Sofia", newlyCreatedUser.getAddress());
    }

    @Test
    void test_PostRegisterInvalidParams_RedirectsToRegisterForm() throws Exception {
        mockMvc
                .perform(post("/users/register")
                        .param("firstName", "Test")
                        .param("lastName", "Testov")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/register"));
    }

    @Test
    void test_PostLoginCorrectCredentials_LoginUserCorrectly() throws Exception {
        mockMvc
                .perform(post("/users/login")
                        .param("username", "test")
                        .param("password", "12345")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/all"));

    }

    @Test
    void test_PostLoginWrongCredentials_LoginError() throws Exception {
        mockMvc
                .perform(post("/users/login")
                        .param("username", "wrong_username")
                        .param("password", "12345")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(forwardedUrl("/users/login-error"));
    }

    @Test
    @WithMockUser(username = "test")
    void test_ProfilePage_LoadsCorrectly() throws Exception {
        mockMvc
                .perform(get("/users/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }


    private UserRoleEntity initUserRole() {
        return userRoleRepository.save(new UserRoleEntity()
                .setRole(UserRoleEnum.USER));
    }

    private void initTestUser() {
        userRepository.save(new UserEntity()
                .setFirstName("test")
                .setLastName("test")
                .setUsername("test")
                .setEmail("test@test.bg")
                .setAddress("Sofia")
                .setPassword("713ced98f52887220162f4a73fc4109ac9a76bb919a888ffb41fed4f922148b158f84bdef58778a3")
                .setRoles(List.of(userRole)));
    }
}