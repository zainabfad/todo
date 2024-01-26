package project.Todolist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.Todolist.Repository.ResetPasswordRepository;
import project.Todolist.api.model.ResetPassword;
import project.Todolist.api.model.User;
import project.Todolist.api.user.ExpiredOtpException;
import project.Todolist.api.user.InvalidOtpException;
import project.Todolist.api.user.UserRepository;
import project.Todolist.api.user.UserService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordService {

    @Autowired
    private final ResetPasswordRepository resetPasswordRepository;
    @Autowired
    private final UserRepository userRepository;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


public void saveOtp(Long userId, String email, String otp, LocalDateTime creationDateTime, LocalDateTime expirationDateTime) {
    User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

    ResetPassword resetPasswordOtp = new ResetPassword();
    resetPasswordOtp.setUser(user);
    resetPasswordOtp.setEmail(email);
    resetPasswordOtp.setOtp(otp);
    resetPasswordOtp.setCreationDateTime(creationDateTime);
    resetPasswordOtp.setExpirationDateTime(expirationDateTime);
    resetPasswordOtp.setStatus(false);

    resetPasswordRepository.save(resetPasswordOtp);
}

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

public boolean validateOtp(String email, String otp) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Optional<ResetPassword> resetPasswordOptional = resetPasswordRepository.findByUserAndOtp(user, otp);

    if (!resetPasswordOptional.isPresent()) {
        throw new InvalidOtpException("Invalid OTP.");
    }

    ResetPassword resetPasswordOtp = resetPasswordOptional.get();

    if (resetPasswordOtp.getExpirationDateTime().isBefore(LocalDateTime.now())) {
        throw new ExpiredOtpException("OTP has expired");
    }

    resetPasswordOtp.setStatus(true);
    resetPasswordRepository.save(resetPasswordOtp);

    return true;
}







}

