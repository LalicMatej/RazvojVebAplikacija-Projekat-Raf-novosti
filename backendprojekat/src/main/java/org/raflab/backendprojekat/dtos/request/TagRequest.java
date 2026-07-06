package org.raflab.backendprojekat.dtos.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class TagRequest {

    @NotBlank(message = "Naziv taga je obavezan")
    @Size(max = 100, message = "Naziv taga ne sme biti duži od 100 karaktera")
    private String name;

    public TagRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
