package app.egs.shop.controller;

import app.egs.shop.controller.validator.PaginationValidator;
import app.egs.shop.domain.CommentEntity;
import app.egs.shop.exception.BadRequestException;
import app.egs.shop.service.CommentService;
import app.egs.shop.service.ProductService;
import app.egs.shop.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/comment", produces = "application/json")
public class CommentController {
    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentReply> getCommentById(@PathVariable("id") Long comment) {
        var reply = commentService.loadCommentById(comment);
        return ResponseEntity.ok(
            new CommentReply(reply.getId(),
                reply.getUser(),
                reply.getProduct(),
                reply.getText(),
                reply.getRate().name(),
                reply.getStatus().name(),
                reply.getCreated(),
                reply.getChanged()
            ));
    }

    @GetMapping("/{id}/product/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<CommentReply>> getCommentByProduct(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size,
        @PathVariable("id") Long product
    ) {
        var productEntity = productService.loadProduct(product);
        var comments = commentService.loadCommentsByProduct(
            productEntity.getId(), PaginationValidator.validatePaginationOrThrow(page, size));

        return ResponseEntity.ok(comments.map(
            comment -> {
                return new CommentReply(comment.getId(),
                    comment.getUser(),
                    comment.getProduct(),
                    comment.getText(),
                    comment.getRate().name(),
                    comment.getStatus().name(),
                    comment.getCreated(),
                    comment.getChanged());
            })
        );
    }

    @PostMapping("/create/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@Valid @RequestBody CommentRequest request) {
        var user = userService.loadUser(request.getUser());
        var product = productService.loadProduct(request.getProduct());
        commentService.createComment(
            user.getId(),
            product.getId(),
            request.getText(),
            ObjectUtils.isEmpty(
                request.getRate())
                ? CommentEntity.Rate.NAN
                : CommentEntity.Rate.valueOf(request.getRate())
        );
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @PutMapping("/admin/{id}/status/")
    @ResponseStatus(HttpStatus.OK)
    public void acceptCommentByAdmin(@PathVariable("id") Long comment, @RequestParam("status") String status) {
        if (ObjectUtils.isEmpty(status)) throw new BadRequestException(
            String.format("You should fill the 'STATUS', but current is (%s)", status)
        );
        commentService.accept(comment, CommentEntity.Status.valueOf(status));
    }

    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@PathVariable("id") Long comment) {
        commentService.deleteComment(comment);
    }

    @GetMapping("/admin/{id}/user/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<CommentReply>> getCommentsByUser(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size,
        @PathVariable("id") Long user
    ) {
        var userEntity = userService.loadUser(user);
        var comments = commentService.loadCommentsByUser(
            userEntity.getId(), PaginationValidator.validatePaginationOrThrow(page, size));

        return ResponseEntity.ok(comments.map(
            comment -> {
                return new CommentReply(comment.getId(),
                    comment.getUser(),
                    comment.getProduct(),
                    comment.getText(),
                    comment.getRate().name(),
                    comment.getStatus().name(),
                    comment.getCreated(),
                    comment.getChanged());
            })
        );
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class CommentRequest {
        @NotNull
        private Long user;
        @NotNull
        private Long product;
        @NotBlank
        private String text;
        private String rate;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class CommentReply {
        private Long id;
        private Long user;
        private Long product;
        private String text;
        private String rate;
        private String status;
        private Date created;
        private Date changed;
    }


    private final UserService userService;
    private final ProductService productService;
    private final CommentService commentService;
}
