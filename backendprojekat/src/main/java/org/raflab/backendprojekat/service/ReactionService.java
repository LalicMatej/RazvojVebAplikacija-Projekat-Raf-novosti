package org.raflab.backendprojekat.service;

import org.raflab.backendprojekat.dtos.request.ReactionRequest;
import org.raflab.backendprojekat.dtos.response.ReactionResponse;
import org.raflab.backendprojekat.model.CommentReaction;
import org.raflab.backendprojekat.model.NewsReaction;
import org.raflab.backendprojekat.repository.reaction.ReactionRepository;

import javax.inject.Inject;

public class ReactionService {

    @Inject
    private ReactionRepository reactionRepository;

    @Inject
    private NewsService newsService;

    @Inject
    private CommentService commentService;

    public ReactionResponse reactToNews(Long newsId, String sessionId, ReactionRequest request) {
        if (newsService.findEntityById(newsId) == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + newsId + " ne postoji");
        }

        NewsReaction existing = reactionRepository.findNewsReaction(newsId, sessionId);

        if (existing != null && existing.getReactionType() == request.getReactionType()) {
            reactionRepository.deleteNewsReaction(newsId, sessionId);
        } else {
            reactionRepository.saveNewsReaction(newsId, sessionId, request.getReactionType());
        }

        return buildNewsReactionResponse(newsId, sessionId);
    }

    public ReactionResponse getNewsReactions(Long newsId, String sessionId) {
        if (newsService.findEntityById(newsId) == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + newsId + " ne postoji");
        }
        return buildNewsReactionResponse(newsId, sessionId);
    }

    private ReactionResponse buildNewsReactionResponse(Long newsId, String sessionId) {
        int likes = reactionRepository.countNewsLikes(newsId);
        int dislikes = reactionRepository.countNewsDislikes(newsId);
        NewsReaction myReaction = reactionRepository.findNewsReaction(newsId, sessionId);
        NewsReaction.ReactionType myType = myReaction != null ? myReaction.getReactionType() : null;
        return new ReactionResponse(likes, dislikes, myType);
    }

    public ReactionResponse reactToComment(Long commentId, String sessionId, ReactionRequest request) {
        if (commentService.findEntityById(commentId) == null) {
            throw new IllegalArgumentException("Komentar sa ID-em " + commentId + " ne postoji");
        }

        CommentReaction existing = reactionRepository.findCommentReaction(commentId, sessionId);

        if (existing != null && existing.getReactionType().name().equals(request.getReactionType().name())) {
            reactionRepository.deleteCommentReaction(commentId, sessionId);
        } else {
            reactionRepository.saveCommentReaction(commentId, sessionId,
                    CommentReaction.ReactionType.valueOf(request.getReactionType().name()));
        }

        return buildCommentReactionResponse(commentId, sessionId);
    }

    public ReactionResponse getCommentReactions(Long commentId, String sessionId) {
        if (commentService.findEntityById(commentId) == null) {
            throw new IllegalArgumentException("Komentar sa ID-em " + commentId + " ne postoji");
        }
        return buildCommentReactionResponse(commentId, sessionId);
    }

    private ReactionResponse buildCommentReactionResponse(Long commentId, String sessionId) {
        int likes = reactionRepository.countCommentLikes(commentId);
        int dislikes = reactionRepository.countCommentDislikes(commentId);
        CommentReaction myReaction = reactionRepository.findCommentReaction(commentId, sessionId);
        NewsReaction.ReactionType myType = myReaction != null
                ? NewsReaction.ReactionType.valueOf(myReaction.getReactionType().name())
                : null;
        return new ReactionResponse(likes, dislikes, myType);
    }


    public void recordVisit(Long newsId, String sessionId) {
        if (!reactionRepository.hasViewed(newsId, sessionId)) {
            reactionRepository.saveView(newsId, sessionId);
            newsService.recordVisit(newsId);
        }
    }
}
