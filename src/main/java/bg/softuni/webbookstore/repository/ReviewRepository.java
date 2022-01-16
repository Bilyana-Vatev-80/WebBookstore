package bg.softuni.webbookstore.repository;

import bg.softuni.webbookstore.model.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    List<ReviewEntity> findAllByApprovedTrueAndBookId(Long bookId);

    List<ReviewEntity> findAllByApprovedTrueAndAuthorUsername(String username);

    List<ReviewEntity> findAllByApprovedFalse();

    @Transactional
    void deleteAllByBookId(Long bookId);

    @Transactional
    void deleteAllByAddedOnBefore(Instant addedOn);
}
