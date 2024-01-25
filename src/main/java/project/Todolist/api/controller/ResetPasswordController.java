package project.Todolist.api.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import project.Todolist.api.email.otp.OtpReminderService;
import project.Todolist.api.model.ResetPassword;
import project.Todolist.api.model.User;
import project.Todolist.api.user.*;
import project.Todolist.service.ResetPasswordService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;

@RestController
@Component
@RequestMapping("/api/v1/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final ResetPasswordService resetPasswordService;
    @Autowired
    private  final UserRepository userRepository;
    @Autowired
    private  final OtpReminderService otpReminderService;


    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        if (userService.existsByEmail(email)) {
            return ResponseEntity.ok("Email exists");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");
        }
    }

@PostMapping("/generate-otp")
public ResponseEntity<ResetPasswordOtpResponse> generateOtp(@RequestParam String email) {
    User user = resetPasswordService.getUserByEmail(email);

    if (user == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    if (!resetPasswordService.existsByEmail(email)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    String otp = generateRandomOtp();

    LocalDateTime creationDateTime = LocalDateTime.now();
    LocalDateTime expirationDateTime = creationDateTime.plus(5, ChronoUnit.MINUTES);

    resetPasswordService.saveOtp(user.getId(), email, otp, creationDateTime, expirationDateTime);

    ResetPassword resetPassword = new ResetPassword();
    resetPassword.setOtp(otp);
    resetPassword.setCreationDateTime(creationDateTime);
    resetPassword.setExpirationDateTime(expirationDateTime);

    otpReminderService.sendOTPEmail(email, resetPassword);

    ResetPasswordOtpResponse response = new ResetPasswordOtpResponse(otp, creationDateTime, expirationDateTime);
    return ResponseEntity.ok(response);
}

    private String generateRandomOtp() {

        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

//    @PostMapping("/validate-otp")
//    public ResponseEntity<?> validateOtp(@RequestParam String email, @RequestParam String otp) {
//        try {
//            if (resetPasswordService.validateOtp(email, otp)) {
//
//                return ResponseEntity.ok("OTP is valid. User can reset the password.");
//            } else {
//
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
//            }
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
//        } catch (ExpiredOtpException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has expired.");
//        }
//    }
@PostMapping("/validate-otp")
public ResponseEntity<?> validateOtp(@RequestParam String email, @RequestParam String otp) {
    try {
        if (resetPasswordService.validateOtp(email, otp)) {
            return ResponseEntity.ok("OTP is valid. User can reset the password.");
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
        }
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
    } catch (ExpiredOtpException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has expired.");
    } catch (InvalidOtpException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
    }
}


    @PostMapping("/complete-reset")
    public ResponseEntity<String> completePasswordReset(@RequestParam String email,
                                                        @RequestParam String otp,
                                                        @RequestParam String newPassword) {
        try {

            if (resetPasswordService.validateOtp(email, otp)) {

                userService.resetPassword(email, newPassword);

                return ResponseEntity.ok("Password reset successful.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP.");
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (ExpiredOtpException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP has expired.");
        }
    }

}


