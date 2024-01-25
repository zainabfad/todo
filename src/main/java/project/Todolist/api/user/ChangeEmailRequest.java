package project.Todolist.api.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeEmailRequest {
    private String newEmail;
    private String currentPassword;
}
