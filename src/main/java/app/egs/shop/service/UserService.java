package app.egs.shop.service;


import app.egs.shop.domain.UserEntity;
import app.egs.shop.exception.BadRequestException;
import app.egs.shop.exception.NotFoundException;
import app.egs.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * Created by Ebrahim Kh.
 */

@Slf4j
@Service
@CacheConfig(cacheNames = "user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserService {

    @Transactional
    @Caching(
        cacheable = {
            @Cacheable(key = "'userByUsername/' + #username"),
            @Cacheable(key = "'userByEmail/' + #email")
        }
    )
    public UserEntity createUser(String username, String password, Set<UserEntity.Authority> authorities,
                                 String email, String name, String family, Boolean isAdmin) throws BadRequestException {
        var oldUser = userRepository.findByUsernameAndStatusIn(username,
            List.of(UserEntity.Status.ACTIVE, UserEntity.Status.PENDING));
        if (oldUser.isPresent()) throw new BadRequestException("Your username created before, you can try to login");
        var user = UserEntity.builder()
            .username(username)
            .password(password)
            .name(name)
            .family(family)
            .email(email);
        if (isAdmin) {
            user.status(UserEntity.Status.ACTIVE);
            user.authorities(authorities);
        } else {
            user.status(UserEntity.Status.PENDING);
            user.authorities(Set.of(UserEntity.Authority.USER));
        }
        var save = userRepository.save(user.build());
        log.debug("The user has saved: {}", save);
        return save;
    }


    @Caching(evict = {
        @CacheEvict(key = "'userById/' + #user.toString()"),
        @CacheEvict(key = "'userByUsername/' + #username"),
        @CacheEvict(key = "'userByEmail/' + #email")
    })
    @Transactional
    public Optional<UserEntity> editUser(Long user, String username, String email, String name, String family, UserEntity.Status status,
                                         Set<UserEntity.Authority> authorities, Boolean isAdmin) {
        return Optional.of(userRepository.findById(user))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                if (username != null && !username.isEmpty() && !username.isBlank())
                    reply.setUsername(username);
                if (email != null && !email.isEmpty() && !email.isBlank())
                    reply.setEmail(email.toLowerCase());
                if (name != null && !name.isEmpty() && !name.isBlank())
                    reply.setName(name);
                if (family != null && !family.isEmpty() && !family.isBlank())
                    reply.setFamily(family);
                if (status != null && isAdmin)
                    reply.setStatus(status);
                if (authorities != null && !authorities.isEmpty() && isAdmin)
                    reply.setAuthorities(authorities);
                var save = userRepository.save(reply);
                log.debug("The user has been edited: {}", save);
                return save;
            });
    }


    @Cacheable(key = "'userById/' + #user.toString()")
    @Transactional(readOnly = true)
    public UserEntity loadUser(Long user) {
        return Optional.of(getOptionalUserEntity(user))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(
                userEntity -> UserEntity.Status.ACTIVE.equals(userEntity.getStatus())
                    || UserEntity.Status.PENDING.equals(userEntity.getStatus())
            )
            .orElseThrow(() -> new NotFoundException("User (" + user + ") not found."));
    }

    @Cacheable(key = "'userByUsername/' + #username")
    @Transactional(readOnly = true)
    public UserEntity loadByUsername(String username) {
        return Optional.of(userRepository
            .findByUsernameAndStatusIn(username,
                List.of(UserEntity.Status.ACTIVE, UserEntity.Status.PENDING))
        )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException("Username (" + username + ") not found."));
    }

    private Optional<UserEntity> getOptionalUserEntity(Long user) {
        return userRepository.findByIdAndStatusIn(user, List.of(UserEntity.Status.ACTIVE, UserEntity.Status.PENDING));
    }

    @Transactional(readOnly = true)
    public Page<UserEntity> loadUsers(Pageable pageable) {
        return userRepository.findAllByStatusIn(List.of(UserEntity.Status.ACTIVE, UserEntity.Status.PENDING), pageable);
    }


    @Transactional(readOnly = true)
    public UserEntity authenticateByUsernameAndPassword(String username, String password, Boolean isRemember, String ipAddress) {
        return Optional.of(userRepository
            .findByUsernameAndPasswordAndStatusIn(username, password,
                List.of(UserEntity.Status.ACTIVE, UserEntity.Status.PENDING)))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new BadRequestException(String.format("The username (%s) not found.", username)));
    }

    /* danger it must only by admin invoke */
    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void deleteUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    userRepository.delete(entity);
                    log.debug("Deleted User: {}", entity);
                }
            );
    }

    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void safeDeleteUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    entity.setStatus(UserEntity.Status.DELETED);
                    userRepository.save(entity);
                    log.debug("Safe deleted User --> from:({}) to:({}).", entity.getStatus(), UserEntity.Status.DELETED.name());
                }
            );
    }

    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void banUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    entity.setStatus(UserEntity.Status.BANNED);
                    userRepository.save(entity);
                    log.debug("Banned User --> from:({}) to:({}).", entity.getStatus(), UserEntity.Status.BANNED.name());
                }
            );
    }

    @CacheEvict(key = "'userById/' + #user.toString()")
    @Transactional
    public void frozenUser(Long user) {
        userRepository
            .findById(user)
            .ifPresent(
                entity -> {
                    entity.setStatus(UserEntity.Status.FROZEN);
                    userRepository.save(entity);
                    log.debug("Frozen User --> from:({}) to:({}).", entity.getStatus(), UserEntity.Status.FROZEN.name());
                }
            );
    }

    private final UserRepository userRepository;
}
