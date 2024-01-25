package project.Todolist.api.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder

public class ResetPasswordOtpResponse {

    private String otp;
    private LocalDateTime creationDateTime;
    private LocalDateTime expirationDateTime;

    public ResetPasswordOtpResponse(String otp, LocalDateTime creationDateTime, LocalDateTime expirationDateTime) {
        this.otp = otp;
        this.creationDateTime = creationDateTime;
        this.expirationDateTime = expirationDateTime;
    }

}
