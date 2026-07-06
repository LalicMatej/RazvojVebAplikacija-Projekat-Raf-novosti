package org.raflab.backendprojekat.repository.news;

import org.raflab.backendprojekat.model.Category;
import org.raflab.backendprojekat.model.News;
import org.raflab.backendprojekat.model.Tag;
import org.raflab.backendprojekat.model.User;
import org.raflab.backendprojekat.repository.MySqlAbstractRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MySqlNewsRepository extends MySqlAbstractRepository implements NewsRepository {

    private static final String BASE_SELECT =
            "SELECT n.id, n.title, n.content, n.published_at, n.visit_count, " +
                    "       c.id AS cat_id, c.name AS cat_name, c.description AS cat_desc, " +
                    "       u.id AS user_id, u.first_name, u.last_name, u.email, u.role, u.status " +
                    "FROM news n " +
                    "JOIN categories c ON n.category_id = c.id " +
                    "JOIN users u ON n.author_id = u.id ";

    @Override
    public News create(News news) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            String[] generatedColumns = {"id"};
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO news (title, content, published_at, visit_count, category_id, author_id) VALUES (?, ?, ?, 0, ?, ?)",
                    generatedColumns
            );
            preparedStatement.setString(1, news.getTitle());
            preparedStatement.setString(2, news.getContent());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setLong(4, news.getCategory().getId());
            preparedStatement.setLong(5, news.getAuthor().getId());
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                news.setId(resultSet.getLong(1));
            }

            saveTags(connection, news);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public News findById(Long id) {
        News news = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(BASE_SELECT + "WHERE n.id = ?");
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                news = mapRow(resultSet);
            }
            if (news != null) {
                news.setTags(loadTags(connection, news.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public List<News> findAll(int page, int pageSize) {
        return findWithQuery(BASE_SELECT + "ORDER BY n.published_at DESC LIMIT ? OFFSET ?", page, pageSize);
    }

    @Override
    public int countAll() {
        return countWithQuery("SELECT COUNT(*) FROM news", null);
    }

    @Override
    public List<News> findByCategory(Long categoryId, int page, int pageSize) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            int offset = (page - 1) * pageSize;
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT + "WHERE n.category_id = ? ORDER BY n.published_at DESC LIMIT ? OFFSET ?"
            );
            preparedStatement.setLong(1, categoryId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                News n = mapRow(resultSet);
                n.setTags(loadTags(connection, n.getId()));
                news.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public int countByCategory(Long categoryId) {
        return countWithQuery("SELECT COUNT(*) FROM news WHERE category_id = ?", categoryId);
    }

    @Override
    public List<News> search(String query, int page, int pageSize) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            int offset = (page - 1) * pageSize;
            String like = "%" + query + "%";
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT + "WHERE n.title LIKE ? OR n.content LIKE ? ORDER BY n.published_at DESC LIMIT ? OFFSET ?"
            );
            preparedStatement.setString(1, like);
            preparedStatement.setString(2, like);
            preparedStatement.setInt(3, pageSize);
            preparedStatement.setInt(4, offset);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                News n = mapRow(resultSet);
                n.setTags(loadTags(connection, n.getId()));
                news.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public int countSearch(String query) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            String like = "%" + query + "%";
            preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM news WHERE title LIKE ? OR content LIKE ?"
            );
            preparedStatement.setString(1, like);
            preparedStatement.setString(2, like);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return 0;
    }

    @Override
    public List<News> findLatest(int limit) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT + "ORDER BY n.published_at DESC LIMIT ?"
            );
            preparedStatement.setInt(1, limit);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                News n = mapRow(resultSet);
                n.setTags(loadTags(connection, n.getId()));
                news.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public List<News> findMostRead(int limit, int days) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT +
                            "WHERE n.published_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                            "ORDER BY n.visit_count DESC LIMIT ?"
            );
            preparedStatement.setInt(1, days);
            preparedStatement.setInt(2, limit);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                News n = mapRow(resultSet);
                n.setTags(loadTags(connection, n.getId()));
                news.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public List<News> findByTag(Long tagId, int page, int pageSize) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            int offset = (page - 1) * pageSize;
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT +
                            "JOIN news_tags nt ON n.id = nt.news_id " +
                            "WHERE nt.tag_id = ? ORDER BY n.published_at DESC LIMIT ? OFFSET ?"
            );
            preparedStatement.setLong(1, tagId);
            preparedStatement.setInt(2, pageSize);
            preparedStatement.setInt(3, offset);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                News n = mapRow(resultSet);
                n.setTags(loadTags(connection, n.getId()));
                news.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public int countByTag(Long tagId) {
        return countWithQuery("SELECT COUNT(*) FROM news_tags WHERE tag_id = ?", tagId);
    }

    @Override
    public List<News> findRelatedByTags(Long newsId, int limit) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT +
                            "JOIN news_tags nt ON n.id = nt.news_id " +
                            "WHERE nt.tag_id IN (SELECT tag_id FROM news_tags WHERE news_id = ?) " +
                            "  AND n.id <> ? " +
                            "GROUP BY n.id " +
                            "ORDER BY n.published_at DESC LIMIT ?"
            );
            preparedStatement.setLong(1, newsId);
            preparedStatement.setLong(2, newsId);
            preparedStatement.setInt(3, limit);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                news.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public List<News> findMostReacted(int limit) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(
                    BASE_SELECT +
                            "LEFT JOIN news_reactions nr ON n.id = nr.news_id " +
                            "GROUP BY n.id, c.id, u.id " +
                            "ORDER BY COUNT(nr.id) DESC LIMIT ?"
            );
            preparedStatement.setInt(1, limit);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                news.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    @Override
    public News update(News news) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(
                    "UPDATE news SET title = ?, content = ?, category_id = ? WHERE id = ?"
            );
            preparedStatement.setString(1, news.getTitle());
            preparedStatement.setString(2, news.getContent());
            preparedStatement.setLong(3, news.getCategory().getId());
            preparedStatement.setLong(4, news.getId());
            preparedStatement.executeUpdate();

            deleteTagLinks(connection, news.getId());
            saveTags(connection, news);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return findById(news.getId());
    }

    @Override
    public boolean delete(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement("DELETE FROM news WHERE id = ?");
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return false;
    }

    @Override
    public boolean incrementVisitCount(Long newsId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(
                    "UPDATE news SET visit_count = visit_count + 1 WHERE id = ?"
            );
            preparedStatement.setLong(1, newsId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return false;
    }

    private void saveTags(Connection connection, News news) throws SQLException {
        if (news.getTags() == null || news.getTags().isEmpty()) return;

        for (Tag tag : news.getTags()) {
            long tagId = findOrCreateTag(connection, tag.getName());

            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(
                        "INSERT IGNORE INTO news_tags (news_id, tag_id) VALUES (?, ?)"
                );
                ps.setLong(1, news.getId());
                ps.setLong(2, tagId);
                ps.executeUpdate();
            } finally {
                this.closeStatement(ps);
            }
        }
    }

    private long findOrCreateTag(Connection connection, String tagName) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement("SELECT id FROM tags WHERE name = ?");
            ps.setString(1, tagName);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
        }

        PreparedStatement insert = null;
        ResultSet keys = null;
        try {
            insert = connection.prepareStatement(
                    "INSERT INTO tags (name) VALUES (?)", new String[]{"id"}
            );
            insert.setString(1, tagName);
            insert.executeUpdate();
            keys = insert.getGeneratedKeys();
            if (keys.next()) return keys.getLong(1);
        } finally {
            this.closeResultSet(keys);
            this.closeStatement(insert);
        }

        throw new SQLException("Could not find or create tag: " + tagName);
    }

    private void deleteTagLinks(Connection connection, Long newsId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("DELETE FROM news_tags WHERE news_id = ?");
            ps.setLong(1, newsId);
            ps.executeUpdate();
        } finally {
            this.closeStatement(ps);
        }
    }

    private List<Tag> loadTags(Connection connection, Long newsId) throws SQLException {
        List<Tag> tags = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(
                    "SELECT t.id, t.name FROM tags t JOIN news_tags nt ON t.id = nt.tag_id WHERE nt.news_id = ?"
            );
            ps.setLong(1, newsId);
            rs = ps.executeQuery();
            while (rs.next()) {
                tags.add(new Tag(rs.getLong("id"), rs.getString("name")));
            }
        } finally {
            this.closeResultSet(rs);
            this.closeStatement(ps);
        }
        return tags;
    }

    private List<News> findWithQuery(String sql, int page, int pageSize) {
        List<News> news = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            int offset = (page - 1) * pageSize;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, offset);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                News n = mapRow(resultSet);
                n.setTags(loadTags(connection, n.getId()));
                news.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return news;
    }

    private int countWithQuery(String sql, Long param) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();
            preparedStatement = connection.prepareStatement(sql);
            if (param != null) preparedStatement.setLong(1, param);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return 0;
    }

    private News mapRow(ResultSet rs) throws SQLException {
        News news = new News();
        news.setId(rs.getLong("id"));
        news.setTitle(rs.getString("title"));
        news.setContent(rs.getString("content"));
        news.setPublishedAt(rs.getTimestamp("published_at").toLocalDateTime());
        news.setVisitCount(rs.getInt("visit_count"));

        Category category = new Category(
                rs.getLong("cat_id"),
                rs.getString("cat_name"),
                rs.getString("cat_desc")
        );
        news.setCategory(category);

        User author = new User();
        author.setId(rs.getLong("user_id"));
        author.setFirstName(rs.getString("first_name"));
        author.setLastName(rs.getString("last_name"));
        author.setEmail(rs.getString("email"));
        author.setRole(User.Role.valueOf(rs.getString("role")));
        author.setStatus(User.Status.valueOf(rs.getString("status")));
        news.setAuthor(author);

        return news;
    }
}
