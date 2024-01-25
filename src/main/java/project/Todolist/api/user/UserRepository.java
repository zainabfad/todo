package project.Todolist.api.user;

import org.springframework.data.jpa.repository.JpaRepository;
import project.Todolist.api.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);


}
