package project.Todolist.api.user;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteAccountRequest {
    private String currentPassword;
}
