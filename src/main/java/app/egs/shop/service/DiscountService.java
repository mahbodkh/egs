package app.egs.shop.service;

import app.egs.shop.domain.CartEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */


@Slf4j
@Service
@CacheConfig(cacheNames = "discount")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DiscountService {

    public DiscountEntity applyPromotion(CartEntity cart) {
        return new DiscountEntity(BigDecimal.ZERO, DiscountEntity.Type.UNKNOWN);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class DiscountEntity {
        private BigDecimal rate;
        private Type type;

        public enum Type {
            PERCENTAGE,
            PRICE,
            UNKNOWN
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var that = (DiscountEntity) o;
            return type == that.type && Objects.equals(rate, that.rate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, rate);
        }
    }
}
