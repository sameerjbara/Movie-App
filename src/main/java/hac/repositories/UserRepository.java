package hac.repositories;

import hac.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return The user with the specified username, or null if not found.
     */
    User findByUsername(String username);

    /**
     * Finds a user by their email.
     *
     * @param email The email to search for.
     * @return The user with the specified email, or null if not found.
     */
    User findByEmail(String email);


    User findById(long id);



}

