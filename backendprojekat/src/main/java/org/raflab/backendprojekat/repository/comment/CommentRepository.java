package org.raflab.backendprojekat.repository.comment;

import org.raflab.backendprojekat.model.Comment;

import java.util.List;

public interface CommentRepository {
    Comment create(Comment comment);
    Comment findById(Long id);
    List<Comment> findByNews(Long newsId, int page, int pageSize);
    int countByNews(Long newsId);
    boolean delete(Long id);
}
