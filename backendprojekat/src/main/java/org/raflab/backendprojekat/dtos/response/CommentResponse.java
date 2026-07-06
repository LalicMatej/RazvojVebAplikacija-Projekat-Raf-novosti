package org.raflab.backendprojekat.dtos.response;

import org.raflab.backendprojekat.model.Comment;

import java.time.LocalDateTime;

public class CommentResponse {

    private Long id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private Long newsId; // samo ID vesti, ne ceo NewsResponse da izbegnemo ciklicno ugnjezdavanje

    public CommentResponse() {}

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.authorName = comment.getAuthorName();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.newsId = comment.getNewsId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }
}
