package org.raflab.backendprojekat.repository.reaction;

import org.raflab.backendprojekat.model.CommentReaction;
import org.raflab.backendprojekat.model.NewsReaction;
import org.raflab.backendprojekat.repository.MySqlAbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlReactionRepository extends MySqlAbstractRepository implements ReactionRepository {

    // -------------------------------------------------------------------------
    // News reactions
    // -------------------------------------------------------------------------

    @Override
    public NewsReaction findNewsReaction(Long newsId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "SELECT * FROM news_reactions WHERE news_id = ? AND session_id = ?"
            );
            ps.setLong(1, newsId);
            ps.setString(2, sessionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new NewsReaction(
                        rs.getLong("id"),
                        rs.getLong("news_id"),
                        rs.getString("session_id"),
                        NewsReaction.ReactionType.valueOf(rs.getString("reaction_type"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
        return null;
    }

    @Override
    public NewsReaction saveNewsReaction(Long newsId, String sessionId, NewsReaction.ReactionType type) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "INSERT INTO news_reactions (news_id, session_id, reaction_type) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE reaction_type = VALUES(reaction_type)",
                    new String[]{"id"}
            );
            ps.setLong(1, newsId);
            ps.setString(2, sessionId);
            ps.setString(3, type.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
        return findNewsReaction(newsId, sessionId);
    }

    @Override
    public void deleteNewsReaction(Long newsId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "DELETE FROM news_reactions WHERE news_id = ? AND session_id = ?"
            );
            ps.setLong(1, newsId);
            ps.setString(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
    }

    @Override
    public int countNewsLikes(Long newsId) {
        return countReactions("news_reactions", "news_id", newsId, "LIKE");
    }

    @Override
    public int countNewsDislikes(Long newsId) {
        return countReactions("news_reactions", "news_id", newsId, "DISLIKE");
    }

    // -------------------------------------------------------------------------
    // Comment reactions
    // -------------------------------------------------------------------------

    @Override
    public CommentReaction findCommentReaction(Long commentId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "SELECT * FROM comment_reactions WHERE comment_id = ? AND session_id = ?"
            );
            ps.setLong(1, commentId);
            ps.setString(2, sessionId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new CommentReaction(
                        rs.getLong("id"),
                        rs.getLong("comment_id"),
                        rs.getString("session_id"),
                        CommentReaction.ReactionType.valueOf(rs.getString("reaction_type"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
        return null;
    }

    @Override
    public CommentReaction saveCommentReaction(Long commentId, String sessionId, CommentReaction.ReactionType type) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "INSERT INTO comment_reactions (comment_id, session_id, reaction_type) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE reaction_type = VALUES(reaction_type)"
            );
            ps.setLong(1, commentId);
            ps.setString(2, sessionId);
            ps.setString(3, type.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
        return findCommentReaction(commentId, sessionId);
    }

    @Override
    public void deleteCommentReaction(Long commentId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "DELETE FROM comment_reactions WHERE comment_id = ? AND session_id = ?"
            );
            ps.setLong(1, commentId);
            ps.setString(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
    }

    @Override
    public int countCommentLikes(Long commentId) {
        return countReactions("comment_reactions", "comment_id", commentId, "LIKE");
    }

    @Override
    public int countCommentDislikes(Long commentId) {
        return countReactions("comment_reactions", "comment_id", commentId, "DISLIKE");
    }

    // -------------------------------------------------------------------------
    // News views
    // -------------------------------------------------------------------------

    @Override
    public boolean hasViewed(Long newsId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "SELECT id FROM news_views WHERE news_id = ? AND session_id = ?"
            );
            ps.setLong(1, newsId);
            ps.setString(2, sessionId);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
        return false;
    }

    @Override
    public void saveView(Long newsId, String sessionId) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "INSERT IGNORE INTO news_views (news_id, session_id) VALUES (?, ?)"
            );
            ps.setLong(1, newsId);
            ps.setString(2, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
    }

    private int countReactions(String table, String idColumn, Long id, String type) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = this.newConnection();
            ps = connection.prepareStatement(
                    "SELECT COUNT(*) FROM " + table + " WHERE " + idColumn + " = ? AND reaction_type = ?"
            );
            ps.setLong(1, id);
            ps.setString(2, type);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
            this.closeConnection(connection);
        }
        return 0;
    }
}
