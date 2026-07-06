package org.raflab.backendprojekat.model;

import java.time.LocalDateTime;

public class Comment {

    private Long id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;

    private Long newsId;

    public Comment() {}

    public Comment(Long id, String authorName, String content,
                   LocalDateTime createdAt, Long newsId) {
        this.id = id;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
        this.newsId = newsId;
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
