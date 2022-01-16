package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(value = "roles")
    Optional<UserEntity> findByUsername(String username);

    @EntityGraph(value = "roles")
    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    @Query("SELECT u.username FROM UserEntity u ORDER BY u.username")
    List<String> findAllUsernames();
}
