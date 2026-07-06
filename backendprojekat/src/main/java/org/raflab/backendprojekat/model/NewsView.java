package org.raflab.backendprojekat.model;

public class NewsView {

    private Long id;
    private Long newsId;
    private String sessionId;

    public NewsView() {}

    public NewsView(Long id, Long newsId, String sessionId) {
        this.id = id;
        this.newsId = newsId;
        this.sessionId = sessionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}

