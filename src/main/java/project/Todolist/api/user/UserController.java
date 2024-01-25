package project.Todolist.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully changed password");
    }

    @GetMapping("/details")
    public ResponseEntity<UserDetailsResponse> getUserDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        UserDetailsResponse userDetailsResponse = service.getUserDetailsByEmail(userEmail);

        return ResponseEntity.ok(userDetailsResponse);
    }

    @PatchMapping("/changeEmail")
    public ResponseEntity<?> changeEmail(
            @RequestBody ChangeEmailRequest request,
            Principal connectedUser
    ) {
        service.changeEmail(request, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).body("Successfully changed email");
    }

    @DeleteMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest request,Principal connectedUser) {

        service.deleteAccount(connectedUser,request);
        return ResponseEntity.status(HttpStatus.OK).body("Account deleted successfully");
    }



}

