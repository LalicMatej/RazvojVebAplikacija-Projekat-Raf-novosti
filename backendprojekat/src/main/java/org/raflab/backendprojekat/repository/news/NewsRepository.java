package org.raflab.backendprojekat.repository.news;

import org.raflab.backendprojekat.model.News;

import java.util.List;

public interface NewsRepository {
    News create(News news);
    News findById(Long id);
    List<News> findAll(int page, int pageSize);
    int countAll();
    List<News> findByCategory(Long categoryId, int page, int pageSize);
    int countByCategory(Long categoryId);
    List<News> search(String query, int page, int pageSize);
    int countSearch(String query);
    List<News> findLatest(int limit);
    List<News> findMostRead(int limit, int days);
    List<News> findByTag(Long tagId, int page, int pageSize);
    int countByTag(Long tagId);
    List<News> findRelatedByTags(Long newsId, int limit);
    List<News> findMostReacted(int limit);
    News update(News news);
    boolean delete(Long id);
    boolean incrementVisitCount(Long newsId);
}
