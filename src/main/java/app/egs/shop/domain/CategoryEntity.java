package app.egs.shop.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Ebrahim Kh.
 */


@Table(name = "\"category\"")
@Data
@NoArgsConstructor
@Entity
public class CategoryEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(max = 50)
    @Column(name = "name")
    private String name;
    @Column(name = "created")
    @CreationTimestamp
    private Date created;
    @Column(name = "changed")
    @UpdateTimestamp
    private Date changed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryEntity that = (CategoryEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(created, that.created) && Objects.equals(changed, that.changed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, created, changed);
    }

    @Builder(toBuilder = true)
    public CategoryEntity(Long id, String name, Date created, Date changed) {
        setId(id);
        setName(name);
        setCreated(created);
        setChanged(changed);
    }

    @Transient
    public static CategoryEntity getBasicCategory(String name) {
        return CategoryEntity.builder()
            .name(name)
            .build();
    }
}
