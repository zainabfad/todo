package project.Todolist.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.Todolist.api.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {


}