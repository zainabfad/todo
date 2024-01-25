package project.Todolist.api.user;

// UserDetailsResponse.java

import lombok.Data;
import project.Todolist.api.model.User;

@Data
public class UserDetailsResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;

    public static UserDetailsResponse fromUser(User user) {
        UserDetailsResponse response = new UserDetailsResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        return response;
    }
}
