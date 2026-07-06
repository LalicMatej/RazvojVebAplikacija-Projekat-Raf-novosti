package org.raflab.backendprojekat.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class News {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private int visitCount;

    private Category category;

    private User author;

    private List<Tag> tags = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    public News() {}

    public News(Long id, String title, String content, LocalDateTime publishedAt,
                int visitCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.visitCount = visitCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }

    public int getVisitCount() { return visitCount; }
    public void setVisitCount(int visitCount) { this.visitCount = visitCount; }
    public void incrementVisitCount() { this.visitCount++; }


    public Category getCategory() { return category; }
    public void setCategory(Category category) {
        this.category = category;
    }

    public User getAuthor() { return author; }
    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments != null ? comments : new ArrayList<>(); }
}
