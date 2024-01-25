package project.Todolist.api.model;

import javax.persistence.*;

import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "resetpassword")
public class ResetPassword {

    @Id
    @SequenceGenerator(
            name= "resetpassword_sequence",
            sequenceName = "resetpassword_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator ="resetpassword_sequence"
    )

    private Long id;


    private String email;
    private String otp;
    private LocalDateTime creationDateTime;
    private LocalDateTime expirationDateTime;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public ResetPassword() {

    }

    public ResetPassword(String email, String otp, LocalDateTime creationDateTime, LocalDateTime expirationDateTime) {
        this.email = email;
        this.otp = otp;
        this.creationDateTime = creationDateTime;
        this.expirationDateTime = expirationDateTime;
    }
}
