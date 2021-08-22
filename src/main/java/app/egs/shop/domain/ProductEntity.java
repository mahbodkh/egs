package app.egs.shop.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */

@Table(name = "\"product\"")
@Data
@NoArgsConstructor
@Entity
public class ProductEntity {

    public enum Type {
        MAIN,
        SIDE
    }

    public enum Status {
        AVAILABLE,      // in Stock
        DISCONTINUE,    //
        PENDING,        // Out Of Stock
        BANNED          //
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(name = "name")
    @NotBlank
    private String name;
    @Column(name = "category")
    @NotNull
    private Long category;
    @Column(name = "description")
    @Size(max = 200)
    private String description;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;


    @Builder(toBuilder = true)
    public ProductEntity(Long id, String name, Long category ,String description, Type type,
                         BigDecimal price, Status status, Date created, Date changed) {
        setId(id);
        setName(name);
        setCategory(category);
        setDescription(description);
        setPrice(price);
        setType(type);
        setStatus(status);
        setCreated(created);
        setChanged(changed);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductEntity that = (ProductEntity) o;
        return
            Objects.equals(name, that.name) &&
                Objects.equals(price, that.price) &&
                Objects.equals(created, that.created) &&
                Objects.equals(changed, that.changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, created, changed);
    }

    @Transient
    public static ProductEntity getBasicProduct(String name, Long category, String description, BigDecimal price, Status status, Type type) {
        return ProductEntity.builder()
            .name(name)
            .category(category)
            .description(description)
            .price(price)
            .type(type)
            .status(status)
            .build();
    }


}
