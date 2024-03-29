package project.Todolist.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import project.Todolist.api.Enums.TaskEnum;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table
@Data
@RequiredArgsConstructor

public class Task  {
    @Id
    @SequenceGenerator(
            name= "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
        generator ="task_sequence"
    )

    private Long id;
    private String title;
    private String description;
    private boolean completed ;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private TaskEnum priority;
    private LocalDateTime reminder;

@ManyToOne(fetch = FetchType.EAGER )
@JoinColumn(name = "todolist_id")

private TodoList todoList;

@Column(name = "reminder_sent")
private boolean reminderSent=false;
private boolean archived;
@ManyToOne(fetch = FetchType.EAGER )
@JoinColumn(name = "user_id")
@JsonIgnore
private User user;


}