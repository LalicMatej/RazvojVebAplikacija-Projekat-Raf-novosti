package org.raflab.backendprojekat.model;

public class User {

    public enum Role {
        CONTENT_CREATOR, ADMIN
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private Role role;
    private Status status;

    public User() {}

    public User(Long id, String firstName, String lastName, String email,
                String passwordHash, Role role, Status status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isActive() { return Status.ACTIVE.equals(this.status); }
    public boolean isAdmin() { return Role.ADMIN.equals(this.role); }
}
