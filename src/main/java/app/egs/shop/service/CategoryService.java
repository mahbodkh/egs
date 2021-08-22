package app.egs.shop.service;

import app.egs.shop.domain.CategoryEntity;
import app.egs.shop.exception.NotFoundException;
import app.egs.shop.repository.CategoryRepository;
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

import java.util.Optional;

/**
 * Created by Ebrahim Kh.
 */

@Slf4j
@Service
@CacheConfig(cacheNames = "category")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryService {

    @Transactional
    @Cacheable(key = "'categoryByName/' + #name")
    public CategoryEntity createCategory(String name) {
        var category = CategoryEntity.getBasicCategory(name);
        var save = categoryRepository.save(category);
        log.debug("The category has been persisted: ({})", save);
        return save;
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'categoryById/' + #category.toString()")
    public CategoryEntity loadCategory(Long category) {
        return Optional.of(categoryRepository.findById(category))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new NotFoundException("The category (" + category + ") not found."));
    }


    @Transactional(readOnly = true)
    public Page<CategoryEntity> loadCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Caching(evict = {
        @CacheEvict(key = "'categoryById/' + #category.toString()"),
        @CacheEvict(key = "'categoryByName/' + #name"),
    })
    @Transactional
    public Optional<CategoryEntity> editCategory(Long category, String name) {
        return Optional.of(categoryRepository.findById(category))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(reply -> {
                if (name != null && !name.isEmpty() && !name.isBlank())
                    reply.setName(name);
                var save = categoryRepository.save(reply);
                log.debug("The category has been edited: {}", save);
                return save;
            });
    }


    @CacheEvict(key = "'categoryById/' + #product.toString()")
    @Transactional
    public void deleteCategory(Long product) {
        categoryRepository
            .findById(product)
            .ifPresent(
                entity -> {
                    categoryRepository.delete(entity);
                    log.debug("Deleted category: {}", entity);
                }
            );
    }

    private final CategoryRepository categoryRepository;
}
