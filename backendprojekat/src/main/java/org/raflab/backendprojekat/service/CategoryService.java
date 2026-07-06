package org.raflab.backendprojekat.service;

import org.raflab.backendprojekat.dtos.request.CategoryRequest;
import org.raflab.backendprojekat.dtos.response.CategoryResponse;
import org.raflab.backendprojekat.dtos.response.PageResponse;
import org.raflab.backendprojekat.model.Category;
import org.raflab.backendprojekat.repository.category.CategoryRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryService {

    @Inject
    private CategoryRepository categoryRepository;

    private static final int PAGE_SIZE = 10;

    public CategoryResponse create(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new IllegalArgumentException("Kategorija sa nazivom '" + request.getName() + "' već postoji");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return new CategoryResponse(categoryRepository.create(category));
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id);
        if (category == null) {
            throw new IllegalArgumentException("Kategorija sa ID-em " + id + " ne postoji");
        }
        return new CategoryResponse(category);
    }

    public PageResponse<CategoryResponse> findAll(int page) {
        List<CategoryResponse> categories = categoryRepository.findAll(page, PAGE_SIZE)
                .stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
        int total = categoryRepository.countAll();
        return new PageResponse<>(categories, page, PAGE_SIZE, total);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Kategorija sa ID-em " + id + " ne postoji");
        }

        Category byName = categoryRepository.findByName(request.getName());
        if (byName != null && !byName.getId().equals(id)) {
            throw new IllegalArgumentException("Kategorija sa nazivom '" + request.getName() + "' već postoji");
        }

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        return new CategoryResponse(categoryRepository.update(existing));
    }

    public void delete(Long id) {
        if (categoryRepository.findById(id) == null) {
            throw new IllegalArgumentException("Kategorija sa ID-em " + id + " ne postoji");
        }
        if (categoryRepository.hasNews(id)) {
            throw new IllegalArgumentException("Nije moguće obrisati kategoriju koja sadrži vesti");
        }
        categoryRepository.delete(id);
    }

    public CategoryResponse findByName(String name) {
        Category category = categoryRepository.findByName(name);
        if (category == null) {
            throw new IllegalArgumentException("Kategorija sa nazivom '" + name + "' ne postoji");
        }
        return new CategoryResponse(category);
    }

    public Category findEntityById(Long id) {
        return categoryRepository.findById(id);
    }
}
