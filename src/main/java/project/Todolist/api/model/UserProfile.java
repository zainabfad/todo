package project.Todolist.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table
public class UserProfile {
    @Id
    @SequenceGenerator(
            name = "userProfile_sequence",
            sequenceName = "userProfile_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "userProfile_sequence"
    )
    private Long id;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)  // Ensure a unique constraint
    @JsonBackReference
    private User user;

    public UserProfile() {

    }

    public UserProfile(Long id, String firstname, String lastname, String username, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;

    }

    public UserProfile(String firstname, String lastname, String username, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        ;
    }
}


