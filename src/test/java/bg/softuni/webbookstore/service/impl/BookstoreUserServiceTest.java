package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.UserEntity;
import bg.softuni.webbookstore.model.entity.UserRoleEntity;
import bg.softuni.webbookstore.model.entity.enums.UserRoleEnum;
import bg.softuni.webbookstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookstoreUserServiceTest {

    private UserEntity testUser;
    private UserRoleEntity adminRole, userRole;

    private BookstoreUserService bookstoreUserServiceToTest;

    @Mock
    private UserRepository mockUserRepository;

    @BeforeEach
    void setUp() {
        bookstoreUserServiceToTest = new BookstoreUserService(mockUserRepository);
        initTestUser();
    }

    @Test
    void test_UserNotFound() {
        assertThrows(
                UsernameNotFoundException.class,
                () -> bookstoreUserServiceToTest.loadUserByUsername("wrong_username")
        );
    }

    @Test
    void test_UserFound() {
        Mockito.when(mockUserRepository.findByUsername(testUser.getUsername()))
                .thenReturn(Optional.of(testUser));

        UserDetails actual = bookstoreUserServiceToTest.loadUserByUsername(testUser.getUsername());

        String expectedRoles = "ROLE_ADMIN, ROLE_USER";
        String actualRoles = actual
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));

        assertEquals(testUser.getUsername(), actual.getUsername());
        assertEquals(expectedRoles, actualRoles);
    }

    private void initTestUser() {
        adminRole = new UserRoleEntity()
                .setRole(UserRoleEnum.ADMIN);
        userRole = new UserRoleEntity()
                .setRole(UserRoleEnum.USER);

        testUser = new UserEntity()
                .setFirstName("test")
                .setLastName("test")
                .setUsername("test")
                .setEmail("test@test.bg")
                .setAddress("Sofia")
                .setPassword("713ced98f52887220162f4a73fc4109ac9a76bb919a888ffb41fed4f922148b158f84bdef58778a3")
                .setRoles(List.of(adminRole, userRole));
    }
}