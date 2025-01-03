package app.egs.shop.domain.util;

import app.egs.shop.domain.UserEntity;

import javax.persistence.AttributeConverter;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;

public class SetAuthorityConverter implements AttributeConverter<Set<UserEntity.Authority>, String> {
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(Set<UserEntity.Authority> stringList) {
        return stringList != null ? String.join(SPLIT_CHAR, stringList.stream().map(Enum::name).collect(Collectors.toSet())) : "";
    }

    @Override
    public Set<UserEntity.Authority> convertToEntityAttribute(String string) {
        return string != null ? Set.of(string.split(SPLIT_CHAR)).stream().map(UserEntity.Authority::valueOf).collect(Collectors.toSet()) : emptySet();
    }
}