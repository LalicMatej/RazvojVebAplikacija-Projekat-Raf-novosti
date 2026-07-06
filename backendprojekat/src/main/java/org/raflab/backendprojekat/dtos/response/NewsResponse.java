package org.raflab.backendprojekat.dtos.response;

import org.raflab.backendprojekat.model.News;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NewsResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishedAt;
    private int visitCount;
    private CategoryResponse category;
    private UserResponse author;
    private List<TagResponse> tags = new ArrayList<>();
    private List<CommentResponse> comments = new ArrayList<>();

    public NewsResponse() {}

    public NewsResponse(News news) {
        this.id = news.getId();
        this.title = news.getTitle();
        this.content = news.getContent();
        this.publishedAt = news.getPublishedAt();
        this.visitCount = news.getVisitCount();

        if (news.getCategory() != null) {
            this.category = new CategoryResponse(news.getCategory());
        }
        if (news.getAuthor() != null) {
            this.author = new UserResponse(news.getAuthor());
        }
        if (news.getTags() != null) {
            this.tags = news.getTags().stream()
                    .map(TagResponse::new)
                    .collect(Collectors.toList());
        }
        if (news.getComments() != null) {
            this.comments = news.getComments().stream()
                    .map(CommentResponse::new)
                    .collect(Collectors.toList());
        }
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

    public CategoryResponse getCategory() { return category; }
    public void setCategory(CategoryResponse category) { this.category = category; }

    public UserResponse getAuthor() { return author; }
    public void setAuthor(UserResponse author) { this.author = author; }

    public List<TagResponse> getTags() { return tags; }
    public void setTags(List<TagResponse> tags) { this.tags = tags; }

    public List<CommentResponse> getComments() { return comments; }
    public void setComments(List<CommentResponse> comments) { this.comments = comments; }
}
