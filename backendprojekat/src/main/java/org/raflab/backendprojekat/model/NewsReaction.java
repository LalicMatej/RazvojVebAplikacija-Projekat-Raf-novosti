package org.raflab.backendprojekat.model;

public class NewsReaction {

    public enum ReactionType {
        LIKE, DISLIKE
    }

    private Long id;
    private Long newsId;
    private String sessionId;
    private ReactionType reactionType;

    public NewsReaction() {}

    public NewsReaction(Long id, Long newsId, String sessionId, ReactionType reactionType) {
        this.id = id;
        this.newsId = newsId;
        this.sessionId = sessionId;
        this.reactionType = reactionType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNewsId() { return newsId; }
    public void setNewsId(Long newsId) { this.newsId = newsId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public ReactionType getReactionType() { return reactionType; }
    public void setReactionType(ReactionType reactionType) { this.reactionType = reactionType; }
}
