package app.egs.shop.repository;

import app.egs.shop.domain.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Ebrahim Kh.
 */

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SearchRepository {

    public List<ProductEntity> search(String value) {
        return entityManager
            .createNativeQuery(
                "SELECT * FROM \"product\" as p" +
                    "        WHERE p.name LIKE lower( :value )" +
                    "        or p.description LIKE lower( :value )" +
                    "        or p.price LIKE :value " +
                    "        ORDER BY p.created DESC \n"
                , ProductEntity.class)
            .setParameter("value", "%" + value + "%")
            .getResultList();
    }

    private final EntityManager entityManager;
}
