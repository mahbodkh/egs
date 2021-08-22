package app.egs.shop.repository;


import app.egs.shop.domain.UserEntity;
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
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameAndStatusIn(String username, List<UserEntity.Status> statuses);
    Optional<UserEntity> findByIdAndStatusIn(Long user, List<UserEntity.Status> statuses);
    Page<UserEntity> findAllByStatusIn(List<UserEntity.Status> statuses, Pageable pageable);
    Optional<UserEntity> findByUsernameAndPasswordAndStatusIn(String username, String password, List<UserEntity.Status> statuses);
}
