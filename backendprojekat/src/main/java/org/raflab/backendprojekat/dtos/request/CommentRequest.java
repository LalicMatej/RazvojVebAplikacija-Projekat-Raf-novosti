package org.raflab.backendprojekat.dtos.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CommentRequest {

    @NotBlank(message = "Ime autora je obavezno")
    @Size(max = 100, message = "Ime autora ne sme biti duže od 100 karaktera")
    private String authorName;

    @NotBlank(message = "Sadržaj komentara je obavezan")
    @Size(max = 2000, message = "Komentar ne sme biti duži od 2000 karaktera")
    private String content;

    public CommentRequest() {}

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
