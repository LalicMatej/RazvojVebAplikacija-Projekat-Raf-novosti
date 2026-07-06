package org.raflab.backendprojekat.dtos.request;

import org.raflab.backendprojekat.model.NewsReaction;

import javax.validation.constraints.NotNull;

public class ReactionRequest {

    @NotNull(message = "Tip reakcije je obavezan (LIKE ili DISLIKE)")
    private NewsReaction.ReactionType reactionType;

    public ReactionRequest() {}

    public NewsReaction.ReactionType getReactionType() { return reactionType; }
    public void setReactionType(NewsReaction.ReactionType reactionType) { this.reactionType = reactionType; }
}
