package org.raflab.backendprojekat.dtos.response;

import org.raflab.backendprojekat.model.Tag;

public class TagResponse {

    private Long id;
    private String name;

    public TagResponse() {}

    public TagResponse(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
