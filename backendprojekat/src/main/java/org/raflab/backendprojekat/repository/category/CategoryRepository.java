package org.raflab.backendprojekat.repository.category;

import org.raflab.backendprojekat.model.Category;

import java.util.List;

public interface CategoryRepository {
    Category create(Category category);
    Category findById(Long id);
    Category findByName(String name);
    List<Category> findAll(int page, int pageSize);
    int countAll();
    Category update(Category category);
    boolean delete(Long id);
    boolean hasNews(Long categoryId);
}
