package app.egs.shop.controller;

import app.egs.shop.controller.validator.PaginationValidator;
import app.egs.shop.exception.BadRequestException;
import app.egs.shop.service.CategoryService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Created by Ebrahim Kh.
 */


@RestController
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping(path = "/v1/category", produces = "application/json")
public class CategoryController {
    // ==============================================
    //                     CLIENT
    // ==============================================
    @GetMapping("/{id}/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CategoryReply> getCategory(@PathVariable("id") Long category) {
        var reply = categoryService.loadCategory(category);
        return ResponseEntity.ok(
            new CategoryReply(
                reply.getId(),
                reply.getName(),
                reply.getCreated(),
                reply.getChanged())
        );
    }

    @GetMapping("/all/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<CategoryReply>> loadAllCategories(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "20") int size
    ) throws BadRequestException {
        var categories = categoryService.loadCategories(PaginationValidator.validatePaginationOrThrow(page, size));
        var reply = categories.map(product ->
            new CategoryReply(product.getId(), product.getName(), product.getCreated(), product.getChanged())
        );
        return ResponseEntity.ok(reply);
    }

    // ==============================================
    //                     ADMIN
    // ==============================================
    @PostMapping("/admin/create/")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCategory(@Valid @RequestBody CategoryRequest request) throws BadRequestException {
        categoryService.createCategory(request.getName());
    }

    @PutMapping("/admin/{id}/edit/")
    @ResponseStatus(HttpStatus.OK)
    public void editProduct(@PathVariable("id") Long product, @Valid @RequestBody CategoryRequest request) {
        categoryService.editCategory(product, request.getName());
    }

    @DeleteMapping("/admin/{id}/delete/")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable("id") Long category) {
        categoryService.deleteCategory(category);
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @Getter
    public static class CategoryRequest {
        @Size(min = 5, max = 50, message = "name must be lower that 50 character.")
        @NotBlank
        private String name;
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    @AllArgsConstructor
    @Getter
    public static class CategoryReply {
        private Long id;
        private String name;
        private Date created;
        private Date changed;
    }


    private final CategoryService categoryService;
}
