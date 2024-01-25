package project.Todolist.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.Todolist.api.model.ResetPassword;
import project.Todolist.api.model.User;

import java.util.Optional;

public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

    ResetPassword findByEmail(String email);
    Optional<ResetPassword> findByUserAndOtp(User user, String otp);

    Optional <ResetPassword>findByEmailAndOtp(String email, String otp);
}

