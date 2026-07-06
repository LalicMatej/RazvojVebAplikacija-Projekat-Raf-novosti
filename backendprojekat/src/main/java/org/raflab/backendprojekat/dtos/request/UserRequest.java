package org.raflab.backendprojekat.dtos.request;

import org.raflab.backendprojekat.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRequest {

    @NotBlank(message = "Ime je obavezno")
    @Size(max = 100, message = "Ime ne sme biti duže od 100 karaktera")
    private String firstName;

    @NotBlank(message = "Prezime je obavezno")
    @Size(max = 100, message = "Prezime ne sme biti duže od 100 karaktera")
    private String lastName;

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email nije u ispravnom formatu")
    @Size(max = 255, message = "Email ne sme biti duži od 255 karaktera")
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    @Size(min = 8, message = "Lozinka mora imati najmanje 8 karaktera")
    private String password;

    @NotNull(message = "Rola je obavezna")
    private User.Role role;

    public UserRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public User.Role getRole() { return role; }
    public void setRole(User.Role role) { this.role = role; }
}
