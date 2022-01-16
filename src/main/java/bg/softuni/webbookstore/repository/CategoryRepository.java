package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.CategoryEntity;
import bg.softuni.webbookstore.model.entity.enums.CategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByCategory(CategoryEnum category);
}
