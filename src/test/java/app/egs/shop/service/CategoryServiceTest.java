package app.egs.shop.service;

import app.egs.shop.domain.CategoryEntity;
import app.egs.shop.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class CategoryServiceTest {
    private @Autowired
    CategoryService categoryService;
    private @Autowired
    CategoryRepository categoryRepository;
    private @Autowired
    CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        categoryRepository.deleteAll();
        cacheManager.getCacheNames().parallelStream().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    @Transactional
    public void testCreateCategory() throws Exception {
        var category = buildCategoryEntity();
        var save = categoryService.createCategory(category.getName());

        var CategoryById = categoryRepository.getById(save.getId());
        assertEquals(save.getId(), CategoryById.getId());
        assertEquals(save.getName(), CategoryById.getName());
    }

    @Test
    @Transactional
    public void testLoadCategory() throws Exception {
        var category = buildCategoryEntity();
        var save = categoryService.createCategory(category.getName());

        var CategoryLoad = categoryService.loadCategory(save.getId());
        assertEquals(save.getId(), CategoryLoad.getId());
        assertEquals(save.getName(), CategoryLoad.getName());
    }

    @Test
    @Transactional
    public void testLoadAllCategories() throws Exception {
        var category = buildCategoryEntity();
        categoryService.createCategory(category.getName());

        var Categoryies = categoryService.loadCategories(Pageable.ofSize(20));

        assertEquals(Categoryies.getContent().size(), 1);
        assertEquals(Categoryies.getTotalElements(), 1);
    }

    @Test
    @Transactional
    public void testEditCategory() throws Exception {
        var category = buildCategoryEntity();
        var save = categoryService.createCategory(category.getName());

        var edit = new CategoryEntity();
        edit.setName("category_edit_name");
        var editSave = categoryService.editCategory(save.getId(), edit.getName()).get();

        assertEquals(save.getId(), editSave.getId());
        assertEquals(edit.getName(), editSave.getName());
    }

    @Test
    @Transactional
    public void testDeleteCategory() throws Exception {
        var category = buildCategoryEntity();
        var save = categoryRepository.save(category);

        categoryService.deleteCategory(save.getId());

        var categoryDeleted = categoryRepository.existsById(save.getId());
        assertFalse(categoryDeleted);
    }


    private CategoryEntity buildCategoryEntity() {
        var category = new CategoryEntity();
        category.setName("category_name");
        return category;
    }
}
