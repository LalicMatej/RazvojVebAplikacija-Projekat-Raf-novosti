package org.raflab.backendprojekat.service;

import org.raflab.backendprojekat.dtos.request.CommentRequest;
import org.raflab.backendprojekat.dtos.response.CommentResponse;
import org.raflab.backendprojekat.dtos.response.PageResponse;
import org.raflab.backendprojekat.model.Comment;
import org.raflab.backendprojekat.repository.comment.CommentRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class CommentService {

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private NewsService newsService;

    private static final int PAGE_SIZE = 10;

    public CommentResponse create(Long newsId, CommentRequest request) {
        if (newsService.findEntityById(newsId) == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + newsId + " ne postoji");
        }

        Comment comment = new Comment();
        comment.setAuthorName(request.getAuthorName());
        comment.setContent(request.getContent());
        comment.setNewsId(newsId);

        return new CommentResponse(commentRepository.create(comment));
    }

    public PageResponse<CommentResponse> findByNews(Long newsId, int page) {
        if (newsService.findEntityById(newsId) == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + newsId + " ne postoji");
        }

        List<CommentResponse> comments = commentRepository.findByNews(newsId, page, PAGE_SIZE)
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
        int total = commentRepository.countByNews(newsId);
        return new PageResponse<>(comments, page, PAGE_SIZE, total);
    }

    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Komentar sa ID-em " + commentId + " ne postoji");
        }
        commentRepository.delete(commentId);
    }

    public Comment findEntityById(Long id) {
        return commentRepository.findById(id);
    }
}

