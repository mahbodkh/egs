package app.egs.shop.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */


@Table(name = "\"comment\"")
@Data
@NoArgsConstructor
@Entity
public class CommentEntity implements Comparable<CommentEntity> {

    @Override
    public int compareTo(CommentEntity o) {
        return getCreated().compareTo(o.getCreated());
    }

    public enum Status {
        SUBMIT,
        APPROVE,
        REJECT,
    }

    public enum Rate {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        NAN
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text")
    @Size(max = 200)
    @NotNull
    private String text;
    @Enumerated(EnumType.STRING)
    @Column(name = "rate")
    private Rate rate;
    @Column(name = "product_id")
    @NotNull
    private Long product;
    @Column(name = "user_id")
    @NotNull
    private Long user;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Status status;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;


    @Builder(toBuilder = true)
    public CommentEntity(Long id, String text, Rate rate, Long product, Long user, Status status, Date created, Date changed) {
        setId(id);
        setText(text);
        setRate(rate);
        setProduct(product);
        setUser(user);
        setStatus(status);
        setCreated(created);
        setChanged(changed);
    }

    @Transient
    public static CommentEntity getBasicComment(Long user, Long product, String text, Rate rate) {
        return CommentEntity.builder()
            .user(user)
            .product(product)
            .text(text)
            .rate(ObjectUtils.isEmpty(rate) ? Rate.NAN : rate)
            .status(Status.SUBMIT)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentEntity that = (CommentEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(product, that.product) && Objects.equals(user, that.user) && Objects.equals(created, that.created) && Objects.equals(changed, that.changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, user, status, created, changed);
    }
}
