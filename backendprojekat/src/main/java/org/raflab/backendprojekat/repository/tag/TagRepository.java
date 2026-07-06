package org.raflab.backendprojekat.repository.tag;

import org.raflab.backendprojekat.model.Tag;

import java.util.List;

public interface TagRepository {
    Tag findById(Long id);
    Tag findByName(String name);
    List<Tag> findAll();
    List<Tag> findByNewsId(Long newsId);
}
