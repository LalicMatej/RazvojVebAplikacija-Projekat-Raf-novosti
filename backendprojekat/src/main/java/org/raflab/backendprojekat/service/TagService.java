package org.raflab.backendprojekat.service;

import org.raflab.backendprojekat.dtos.response.TagResponse;
import org.raflab.backendprojekat.repository.tag.TagRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class TagService {

    @Inject
    private TagRepository tagRepository;

    public List<TagResponse> findAll() {
        return tagRepository.findAll()
                .stream()
                .map(TagResponse::new)
                .collect(Collectors.toList());
    }

    public List<TagResponse> findByNewsId(Long newsId) {
        return tagRepository.findByNewsId(newsId)
                .stream()
                .map(TagResponse::new)
                .collect(Collectors.toList());
    }
}
