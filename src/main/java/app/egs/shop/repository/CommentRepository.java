package app.egs.shop.repository;

import app.egs.shop.domain.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    Page<CommentEntity> findAllByStatusIn(List<CommentEntity.Status> statuses, Pageable pageable);
    Optional<CommentEntity> findByUserAndProductAndStatus(Long user, Long product,CommentEntity.Status status);
    Page<CommentEntity> findAllByUser(Long user, Pageable pageable);
    Page<CommentEntity> findAllByProductAndStatusIs(Long product, CommentEntity.Status status, Pageable pageable);
}
