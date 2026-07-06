package org.raflab.backendprojekat.dtos.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


public class NewsRequest {

    @NotBlank(message = "Naslov vesti je obavezan")
    @Size(max = 255, message = "Naslov ne sme biti duži od 255 karaktera")
    private String title;

    @NotBlank(message = "Sadržaj vesti je obavezan")
    private String content;

    @NotNull(message = "Kategorija je obavezna")
    private Long categoryId;

    @Valid
    @Size(max = 10, message = "Vest ne sme imati više od 10 tagova")
    private List<TagRequest> tags = new ArrayList<>();

    public NewsRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public List<TagRequest> getTags() { return tags; }
    public void setTags(List<TagRequest> tags) { this.tags = tags != null ? tags : new ArrayList<>(); }
}
