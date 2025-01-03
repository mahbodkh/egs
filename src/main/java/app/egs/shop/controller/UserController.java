package app.egs.shop.controller;


import app.egs.shop.controller.validator.PaginationValidator;
import app.egs.shop.domain.UserEntity;
import app.egs.shop.exception.BadRequestException;
import app.egs.shop.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Ebrahim Kh.
 */

@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/user", produces = "application/json")
public class UserController {
    // ==============================================
    //                     GUEST
    // ==============================================
    @PostMapping("/login/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginReply> login(@Valid @RequestBody LoginRequest request) {
        var reply = userService.authenticateByUsernameAndPassword(
            request.getUsername(),
            request.getPassword(),
            request.getIsRemember(),
            request.getIpAddress()
        );
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + "jwt");
        return
            new ResponseEntity<>(new LoginReply(reply.getUsername(), null,
                reply.getAuthorities().stream().map(String::valueOf).collect(Collectors.toUnmodifiableSet())),
                httpHeaders, HttpStatus.OK
            );
    }

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request) throws BadRequestException {
        userService.createUser(
            request.getUsername(),
            request.getPassword(),
            null,
            request.getEmail(),
            request.getName(),
            request.getFamily(),
            Boolean.FALSE);
    }

    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/")
    public ResponseEntity<UserReply> getUser(@PathVariable("id") Long user) {
        var reply = userService.loadUser(user);
        return ResponseEntity.ok(
            new UserReply(
                reply.getId(),
                reply.getUsername(),
                reply.getName(),
                reply.getFamily(),
                reply.getEmail(),
                reply.getCreated(),
                reply.getChanged(),
                reply.getStatus().name(),
                reply.getAuthorities().stream().map(Enum::name).collect(Collectors.toSet()))
        );
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @PostMapping("/admin/create/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUserByAdmin(@Valid @RequestBody UserRequest request) {
        userService.createUser(
            request.getUsername(),
            request.getPassword(),
            request.getAuthorities().stream().map(UserEntity.Authority::valueOf).collect(Collectors.toSet()),
            request.getEmail(),
            request.getName(),
            request.getFamily(),
            Boolean.TRUE);
    }

    @GetMapping("/admin/all/")
    public ResponseEntity<Page<UserReply>> getAllUser(@RequestParam(value = "page", defaultValue = "0") int page,
                                                      @RequestParam(value = "size", defaultValue = "20") int size)
        throws BadRequestException {
        var users = userService.loadUsers(PaginationValidator.validatePaginationOrThrow(page, size));
        var reply = users.map(user ->
            new UserReply(user.getId(),
                user.getUsername(),
                user.getName(),
                user.getFamily(),
                user.getEmail(),
                user.getCreated(),
                user.getChanged(),
                user.getStatus().name(),
                user.getAuthorities().stream().map(Enum::name).collect(Collectors.toSet())
            ));
        return ResponseEntity.ok(reply);
    }

    @PutMapping("/admin/{id}/edit/")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable("id") Long user, @Valid @RequestBody UserController.UserRequest request) {
        userService.editUser(
            user,
            request.getUsername(),
            request.getEmail(),
            request.getName(),
            request.getFamily(),
            ObjectUtils.isEmpty(request.getStatus()) ? null : UserEntity.Status.valueOf(request.getStatus()),
            ObjectUtils.isEmpty(request.getAuthorities()) ? null :
                request.getAuthorities().stream().map(UserEntity.Authority::valueOf).collect(Collectors.toSet()),
            Boolean.TRUE
        );
    }

    @PutMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable("id") Long user) {
        userService.safeDeleteUser(user);
    }

    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteRealUser(@PathVariable("id") Long user) {
        userService.deleteUser(user);
    }

    @PutMapping("/admin/{id}/ban/")
    @ResponseStatus(HttpStatus.OK)
    public void banUser(@PathVariable("id") Long user) {
        userService.banUser(user);
    }

    @PutMapping("/admin/{id}/frozen/")
    @ResponseStatus(HttpStatus.OK)
    public void frozenUser(@PathVariable("id") Long user) {
        userService.frozenUser(user);
    }


    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class UserReply {
        private Long id;
        private String username;
        private String name;
        private String family;
        private String email;
        private Date created;
        private Date changed;
        private String status;
        private Set<String> authorities;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class UserRequest {
        @NotBlank
        @Size(min = 5, max = 50, message = "username must be lower that 50 character.")
        private String username;
        @NotBlank
        @Size(min = 5, max = 50, message = "password must be lower that 50 character.")
        private String password;
        @Size(max = 50, message = "name must be lower that 50 character.")
        private String name;
        @Size(max = 50, message = "family must be lower that 50 character.")
        private String family;
        @Size(max = 50, message = "email must be lower that 50 character.")
        @Email
        private String email;
        @Size(max = 50, message = "status must be lower that 50 character.")
        private String status;
        @Size(max = 50, message = "authorities must be lower that 50 character.")
        private Set<String> authorities;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class LoginReply {
        private String token;
        private Date expire;
        private Set<String> authorities;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class LoginRequest {
        @NotBlank
        @Size(max = 50, message = "username must be lower that 50 character.")
        private String username;
        @NotBlank
        @Size(max = 50, message = "password must be lower that 50 character.")
        private String password;
        private Boolean isRemember;
        @Pattern(regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
        private String ipAddress;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class RegisterRequest {
        @Size(min = 5, max = 50, message = "username must be lower that 50 character.")
        @NotBlank
        private String username;
        @Size(min = 5, max = 50, message = "password must be lower that 50 character.")
        @NotBlank
        private String password;
        @Size(max = 50, message = "name must be lower that 50 character.")
        @NotBlank
        private String name;
        @Size(max = 50, message = "family must be lower that 50 character.")
        @NotBlank
        private String family;
        @Email
        @NotBlank
        @Size(max = 50, message = "email must be lower that 50 character.")
        private String email;
        @Pattern(regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
        private String ipAddress;
    }

    private final UserService userService;
}
