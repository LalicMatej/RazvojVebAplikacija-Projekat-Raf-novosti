package org.raflab.backendprojekat.model;

public class CommentReaction {

    public enum ReactionType {
        LIKE, DISLIKE
    }

    private Long id;
    private Long commentId;
    private String sessionId;
    private ReactionType reactionType;

    public CommentReaction() {}

    public CommentReaction(Long id, Long commentId, String sessionId, ReactionType reactionType) {
        this.id = id;
        this.commentId = commentId;
        this.sessionId = sessionId;
        this.reactionType = reactionType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public ReactionType getReactionType() { return reactionType; }
    public void setReactionType(ReactionType reactionType) { this.reactionType = reactionType; }
}
