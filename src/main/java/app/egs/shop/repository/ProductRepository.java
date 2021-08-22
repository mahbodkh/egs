package app.egs.shop.repository;

import app.egs.shop.domain.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByIdAndStatus(Long id, ProductEntity.Status status);
    Page<ProductEntity> findAllByStatusIn(List<ProductEntity.Status> statuses, Pageable pageable);

    @Query("select p from ProductEntity p " +
        "where p.name like %:value% " +
        "or p.description like %:value% " +
        "or p.price like %:value% " +
        "order by p.created desc ")
    List<ProductEntity> search(@Param("value") String value);
}
