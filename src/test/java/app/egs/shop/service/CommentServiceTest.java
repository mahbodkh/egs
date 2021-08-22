package app.egs.shop.service;

import app.egs.shop.domain.CategoryEntity;
import app.egs.shop.domain.CommentEntity;
import app.egs.shop.domain.ProductEntity;
import app.egs.shop.domain.UserEntity;
import app.egs.shop.repository.CategoryRepository;
import app.egs.shop.repository.CommentRepository;
import app.egs.shop.repository.ProductRepository;
import app.egs.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Ebrahim Kh.
 */

@SpringBootTest
public class CommentServiceTest {
    private @Autowired
    UserRepository userRepository;
    private @Autowired
    ProductRepository productRepository;
    private @Autowired
    CommentService commentService;
    private @Autowired
    CommentRepository commentRepository;
    private @Autowired
    CategoryRepository categoryRepository;
    private @Autowired
    CacheManager cacheManager;

    @BeforeEach
    public void setup() {
        user = userRepository.save(buildUserEntity());
        category = categoryRepository.save(buildCategoryEntity());
        product = productRepository.save(buildProductEntity());
        cacheManager.getCacheNames().parallelStream().forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @Test
    @Transactional
    void testCreateComment() throws Exception {
        var text = "comment_text";
        var comment = commentService.createComment(user.getId(), product.getId(), text, null);

        var commentById = commentRepository.getById(comment.getId());

        assertEquals(user.getId(), commentById.getUser());
        assertEquals(product.getId(), commentById.getProduct());
        assertEquals(text, commentById.getText());
        assertEquals(CommentEntity.Rate.NAN, commentById.getRate());
        assertEquals(CommentEntity.Status.SUBMIT, commentById.getStatus());
    }

    @Test
    @Transactional
    void testLoadCommentById() throws Exception {
        var commentEntity = buildCommentEntity();
        var comment = commentRepository.save(commentEntity);

        var commentById = commentService.loadCommentById(comment.getId());

        assertEquals(user.getId(), commentById.getUser());
        assertEquals(product.getId(), commentById.getProduct());
        assertEquals(commentEntity.getText(), commentById.getText());
        assertEquals(CommentEntity.Rate.NAN, commentById.getRate());
        assertEquals(CommentEntity.Status.SUBMIT, commentById.getStatus());
    }

    @Test
    @Transactional
    void testLoadCommentsByProduct() throws Exception {
        var commentEntity = buildCommentEntity();
        var comment = commentRepository.save(commentEntity);

        var commentsByProduct = commentService.loadCommentsByProduct(product.getId(), Pageable.ofSize(20));

        assertEquals(1, commentsByProduct.getContent().size());
        assertEquals(1, commentsByProduct.getContent().size());
        assertEquals(1, commentsByProduct.getTotalElements());

        assertEquals(comment.getId(), commentsByProduct.getContent().get(0).getId());
        assertEquals(comment.getProduct(), commentsByProduct.getContent().get(0).getProduct());
        assertEquals(comment.getUser(), commentsByProduct.getContent().get(0).getUser());
        assertEquals(comment.getText(), commentsByProduct.getContent().get(0).getText());
        assertEquals(comment.getStatus(), commentsByProduct.getContent().get(0).getStatus());
        assertEquals(comment.getRate(), commentsByProduct.getContent().get(0).getRate());
    }

    @Test
    @Transactional
    void testLoadCommentsByUser() throws Exception {
        var commentEntity = buildCommentEntity();
        var comment = commentRepository.save(commentEntity);

        var commentsByUser = commentService.loadCommentsByUser(user.getId(), Pageable.ofSize(20));

        assertEquals(1, commentsByUser.getContent().size());
        assertEquals(1, commentsByUser.getContent().size());
        assertEquals(1, commentsByUser.getTotalElements());

        assertEquals(comment.getId(), commentsByUser.getContent().get(0).getId());
        assertEquals(comment.getProduct(), commentsByUser.getContent().get(0).getProduct());
        assertEquals(comment.getUser(), commentsByUser.getContent().get(0).getUser());
        assertEquals(comment.getText(), commentsByUser.getContent().get(0).getText());
        assertEquals(comment.getStatus(), commentsByUser.getContent().get(0).getStatus());
        assertEquals(comment.getRate(), commentsByUser.getContent().get(0).getRate());
    }

    @Test
    @Transactional
    void testLoadComments() throws Exception {
        var commentEntity = buildCommentEntity();
        var comment = commentRepository.save(commentEntity);

        var commentByPage = commentService.loadComments(Pageable.ofSize(20));

        assertEquals(1, commentByPage.getContent().size());
        assertEquals(1, commentByPage.getContent().size());
        assertEquals(1, commentByPage.getTotalElements());

        assertEquals(comment.getId(), commentByPage.getContent().get(0).getId());
        assertEquals(comment.getProduct(), commentByPage.getContent().get(0).getProduct());
        assertEquals(comment.getUser(), commentByPage.getContent().get(0).getUser());
        assertEquals(comment.getText(), commentByPage.getContent().get(0).getText());
        assertEquals(comment.getStatus(), commentByPage.getContent().get(0).getStatus());
        assertEquals(comment.getRate(), commentByPage.getContent().get(0).getRate());
    }


    private CommentEntity buildCommentEntity() {
        var comment = new CommentEntity();
        comment.setRate(CommentEntity.Rate.NAN);
        comment.setText("comment_text");
        comment.setUser(user.getId());
        comment.setProduct(product.getId());
        comment.setStatus(CommentEntity.Status.SUBMIT);
        return comment;
    }


    private ProductEntity buildProductEntity() {
        var product = new ProductEntity();
        product.setName("product_name");
        product.setCategory(category.getId());
        product.setDescription("product_description");
        product.setStatus(ProductEntity.Status.AVAILABLE);
        product.setPrice(BigDecimal.valueOf(4));
        product.setType(ProductEntity.Type.MAIN);
        return product;
    }

    private CategoryEntity buildCategoryEntity() {
        var category = new CategoryEntity();
        category.setName("category_name");
        return category;
    }

    private UserEntity buildUserEntity() {
        var user = new UserEntity();
        user.setUsername("username");
        user.setPassword("password");
        user.setName("first_name");
        user.setFamily("last_family");
        user.setAuthorities(Set.of(UserEntity.Authority.USER));
        user.setEmail("email@email.com");
        return user;
    }

    private UserEntity user;
    private ProductEntity product;
    private CategoryEntity category;
}
