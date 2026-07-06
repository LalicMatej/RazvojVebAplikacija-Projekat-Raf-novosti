package org.raflab.backendprojekat.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import org.raflab.backendprojekat.dtos.request.LoginRequest;
import org.raflab.backendprojekat.dtos.request.UpdateUserRequest;
import org.raflab.backendprojekat.dtos.request.UserRequest;
import org.raflab.backendprojekat.dtos.response.PageResponse;
import org.raflab.backendprojekat.dtos.response.UserResponse;
import org.raflab.backendprojekat.model.User;
import org.raflab.backendprojekat.repository.user.UserRepository;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserService {

    @Inject
    private UserRepository userRepository;

    private static final int PAGE_SIZE = 10;
    private static final String JWT_SECRET = "raf_news_secret";

    public Map<String, String> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null || !user.getPasswordHash().equals(DigestUtils.sha256Hex(request.getPassword()))) {
            throw new IllegalArgumentException("Pogrešan email ili lozinka");
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException("Vaš nalog je deaktiviran");
        }

        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 24 * 60 * 60 * 1000); // 24h

        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        String token = JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiresAt)
                .withSubject(user.getEmail())
                .withClaim("firstName", user.getFirstName())
                .withClaim("lastName", user.getLastName())
                .withClaim("role", user.getRole().name())
                .withClaim("userId", user.getId())
                .sign(algorithm);

        Map<String, String> response = new HashMap<>();
        response.put("jwt", token);
        return response;
    }

    public User getAuthenticatedUser(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);

            String email = jwt.getSubject();
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Korisnik sa email adresom '" + request.getEmail() + "' već postoji");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(DigestUtils.sha256Hex(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(User.Status.ACTIVE);

        return new UserResponse(userRepository.create(user));
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Korisnik sa ID-em " + id + " ne postoji");
        }
        return new UserResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("Korisnik sa email adresom '" + email + "' ne postoji");
        }
        return new UserResponse(user);
    }

    public PageResponse<UserResponse> findAll(int page) {
        List<UserResponse> users = userRepository.findAll(page, PAGE_SIZE)
                .stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        int total = userRepository.countAll();
        return new PageResponse<>(users, page, PAGE_SIZE, total);
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        User existing = userRepository.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Korisnik sa ID-em " + id + " ne postoji");
        }

        User byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail != null && !byEmail.getId().equals(id)) {
            throw new IllegalArgumentException("Korisnik sa email adresom '" + request.getEmail() + "' već postoji");
        }

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setRole(request.getRole());

        return new UserResponse(userRepository.update(existing));
    }

    public UserResponse updateStatus(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("Korisnik sa ID-em " + id + " ne postoji");
        }
        if (user.isAdmin()) {
            throw new IllegalArgumentException("Administrator ne može da se deaktivira");
        }

        User.Status newStatus = user.isActive() ? User.Status.INACTIVE : User.Status.ACTIVE;
        return new UserResponse(userRepository.updateStatus(id, newStatus));
    }

    public User findEntityById(Long id) {
        return userRepository.findById(id);
    }

    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
