package org.raflab.backendprojekat.dtos.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CategoryRequest {

    @NotBlank(message = "Naziv kategorije je obavezan")
    @Size(max = 100, message = "Naziv kategorije ne sme biti duži od 100 karaktera")
    private String name;

    @NotBlank(message = "Opis kategorije je obavezan")
    private String description;

    public CategoryRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
