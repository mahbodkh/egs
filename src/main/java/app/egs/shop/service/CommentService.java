package app.egs.shop.service;

import app.egs.shop.domain.CommentEntity;
import app.egs.shop.exception.BadRequestException;
import app.egs.shop.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = "comment")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentService {

    @Transactional
    public CommentEntity createComment(Long user, Long product, String text, CommentEntity.Rate rate) {
        var comment = CommentEntity.getBasicComment(user, product, text, rate);
        var save = commentRepository.save(comment);
        log.debug("The comment has been saved: ({}).", comment);
        return save;
    }


    @Transactional(readOnly = true)
    @Cacheable(key = "'commentById/' + #comment.toString()")
    public CommentEntity loadCommentById(Long comment) {
        return Optional.of(commentRepository.findById(comment))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .orElseThrow(() -> new BadRequestException(String.format("Your comment (%s) has not found.", comment)));
    }

    @Transactional(readOnly = true)
    public Page<CommentEntity> loadCommentsByUser(Long user, Pageable pageable) {
        return commentRepository.findAllByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CommentEntity> loadCommentsByProduct(Long product, Pageable pageable) {
        return commentRepository.findAllByProductAndStatusIs(product, CommentEntity.Status.SUBMIT, pageable);
    }

    @Transactional(readOnly = true)
    public Page<CommentEntity> loadComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Transactional
    @Cacheable(key = "'commentById/' + #comment.toString()")
    public void accept(Long comment, CommentEntity.Status status) {
        commentRepository
            .findById(comment)
            .ifPresent(
                entity -> {
                    entity.setStatus(status);
                    commentRepository.save(entity);
                    log.debug("Change status from:({}) to:({}) comment: ({}).", entity, entity.getStatus(), status);
                }
            );
    }

    @Transactional
    @CacheEvict(key = "'commentById/' + #comment.toString()")
    public void deleteComment(Long comment) {
        commentRepository
            .findById(comment)
            .ifPresent(
                entity -> {
                    commentRepository.delete(entity);
                    log.debug("Deleted comment: ({}).", entity);
                }
            );
    }

    private final CommentRepository commentRepository;
}
