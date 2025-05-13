package com.adama.backoffice.users.repository;

import com.adama.backoffice.users.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    List<User> findByRole(User.Role roleEnum);

    @Query("SELECT u.username FROM User u")
    List<String> findAllUsernames();
}
