package app.egs.shop.repository;

import app.egs.shop.domain.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Ebrahim Kh.
 */


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
