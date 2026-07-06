package org.raflab.backendprojekat.repository.user;

import org.raflab.backendprojekat.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll(int page, int pageSize);
    int countAll();
    User update(User user);
    User updateStatus(Long id, User.Status status);
}
