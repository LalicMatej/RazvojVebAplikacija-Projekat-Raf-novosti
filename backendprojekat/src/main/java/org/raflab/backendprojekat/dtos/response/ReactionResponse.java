package org.raflab.backendprojekat.dtos.response;

import org.raflab.backendprojekat.model.NewsReaction;

public class ReactionResponse {

    private int likes;
    private int dislikes;
    private NewsReaction.ReactionType myReaction;

    public ReactionResponse() {}

    public ReactionResponse(int likes, int dislikes, NewsReaction.ReactionType myReaction) {
        this.likes = likes;
        this.dislikes = dislikes;
        this.myReaction = myReaction;
    }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getDislikes() { return dislikes; }
    public void setDislikes(int dislikes) { this.dislikes = dislikes; }

    public NewsReaction.ReactionType getMyReaction() { return myReaction; }
    public void setMyReaction(NewsReaction.ReactionType myReaction) { this.myReaction = myReaction; }
}
