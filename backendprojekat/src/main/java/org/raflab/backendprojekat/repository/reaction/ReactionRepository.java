package org.raflab.backendprojekat.repository.reaction;

import org.raflab.backendprojekat.model.CommentReaction;
import org.raflab.backendprojekat.model.NewsReaction;

public interface ReactionRepository {

    // --- News reactions ---
    NewsReaction findNewsReaction(Long newsId, String sessionId);
    NewsReaction saveNewsReaction(Long newsId, String sessionId, NewsReaction.ReactionType type);
    void deleteNewsReaction(Long newsId, String sessionId);
    int countNewsLikes(Long newsId);
    int countNewsDislikes(Long newsId);

    // --- Comment reactions ---
    CommentReaction findCommentReaction(Long commentId, String sessionId);
    CommentReaction saveCommentReaction(Long commentId, String sessionId, CommentReaction.ReactionType type);
    void deleteCommentReaction(Long commentId, String sessionId);
    int countCommentLikes(Long commentId);
    int countCommentDislikes(Long commentId);

    // --- News views ---
    boolean hasViewed(Long newsId, String sessionId);
    void saveView(Long newsId, String sessionId);
}
