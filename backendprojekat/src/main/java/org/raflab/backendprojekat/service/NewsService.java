package org.raflab.backendprojekat.service;

import org.raflab.backendprojekat.dtos.request.NewsRequest;
import org.raflab.backendprojekat.dtos.response.NewsResponse;
import org.raflab.backendprojekat.dtos.response.PageResponse;
import org.raflab.backendprojekat.model.Category;
import org.raflab.backendprojekat.model.News;
import org.raflab.backendprojekat.model.Tag;
import org.raflab.backendprojekat.model.User;
import org.raflab.backendprojekat.repository.category.CategoryRepository;
import org.raflab.backendprojekat.repository.news.NewsRepository;
import org.raflab.backendprojekat.repository.user.UserRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class NewsService {

    @Inject
    private NewsRepository newsRepository;

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private UserRepository userRepository;

    private static final int PAGE_SIZE = 10;

    public NewsResponse create(NewsRequest request, User currentUser) {
        Category category = categoryRepository.findById(request.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("Kategorija sa ID-em " + request.getCategoryId() + " ne postoji");
        }

        News news = new News();
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());
        news.setCategory(category);
        news.setAuthor(currentUser);
        news.setTags(mapTags(request));

        return new NewsResponse(newsRepository.create(news));
    }

    public NewsResponse findById(Long id) {
        News news = newsRepository.findById(id);
        if (news == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + id + " ne postoji");
        }
        return new NewsResponse(news);
    }

    public PageResponse<NewsResponse> findAll(int page) {
        List<NewsResponse> news = newsRepository.findAll(page, PAGE_SIZE)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
        int total = newsRepository.countAll();
        return new PageResponse<>(news, page, PAGE_SIZE, total);
    }

    public PageResponse<NewsResponse> findByCategory(Long categoryId, int page) {
        List<NewsResponse> news = newsRepository.findByCategory(categoryId, page, PAGE_SIZE)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
        int total = newsRepository.countByCategory(categoryId);
        return new PageResponse<>(news, page, PAGE_SIZE, total);
    }

    public PageResponse<NewsResponse> search(String query, int page) {
        List<NewsResponse> news = newsRepository.search(query, page, PAGE_SIZE)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
        int total = newsRepository.countSearch(query);
        return new PageResponse<>(news, page, PAGE_SIZE, total);
    }

    public PageResponse<NewsResponse> findByTag(Long tagId, int page) {
        List<NewsResponse> news = newsRepository.findByTag(tagId, page, PAGE_SIZE)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
        int total = newsRepository.countByTag(tagId);
        return new PageResponse<>(news, page, PAGE_SIZE, total);
    }

    public List<NewsResponse> findLatest() {
        return newsRepository.findLatest(10)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
    }

    public List<NewsResponse> findMostRead() {
        List<News> mostRead = newsRepository.findMostRead(10, 30);
        if (mostRead.isEmpty()) {
            mostRead = newsRepository.findMostRead(10, 36500);
        }

        return mostRead.stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
    }

    public List<NewsResponse> findRelated(Long newsId) {
        return newsRepository.findRelatedByTags(newsId, 3)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
    }

    public List<NewsResponse> findMostReacted() {
        return newsRepository.findMostReacted(3)
                .stream()
                .map(NewsResponse::new)
                .collect(Collectors.toList());
    }

    public NewsResponse update(Long id, NewsRequest request, User currentUser) {
        News existing = newsRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + id + " ne postoji");
        }

        boolean isAdmin = currentUser.isAdmin();
        boolean isAuthor = existing.getAuthor().getId().equals(currentUser.getId());
        if (!isAdmin && !isAuthor) {
            throw new SecurityException("Nemate dozvolu da menjate ovu vest");
        }

        Category category = categoryRepository.findById(request.getCategoryId());
        if (category == null) {
            throw new IllegalArgumentException("Kategorija sa ID-em " + request.getCategoryId() + " ne postoji");
        }

        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        existing.setCategory(category);
        existing.setTags(mapTags(request));

        return new NewsResponse(newsRepository.update(existing));
    }

    public void delete(Long id, User currentUser) {
        News existing = newsRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Vest sa ID-em " + id + " ne postoji");
        }

        boolean isAdmin = currentUser.isAdmin();
        boolean isAuthor = existing.getAuthor().getId().equals(currentUser.getId());
        if (!isAdmin && !isAuthor) {
            throw new SecurityException("Nemate dozvolu da obrišete ovu vest");
        }

        newsRepository.delete(id);
    }

    public void recordVisit(Long newsId) {
        newsRepository.incrementVisitCount(newsId);
    }

    public News findEntityById(Long id) {
        return newsRepository.findById(id);
    }

    private List<Tag> mapTags(NewsRequest request) {
        return request.getTags().stream()
                .map(tagRequest -> {
                    Tag tag = new Tag();
                    tag.setName(tagRequest.getName());
                    return tag;
                })
                .collect(Collectors.toList());
    }
}
