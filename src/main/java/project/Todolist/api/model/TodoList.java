package project.Todolist.api.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Entity
@Table
@Data
@Transactional
public class TodoList {
    @Id
    @SequenceGenerator(
            name = "todolist_sequence",
            sequenceName = "todolist_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "todolist_sequence"
    )

    private Long id;
    private String name;

    @OneToMany(mappedBy = "todoList", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Task> tasks;

    @ManyToOne(fetch = FetchType.LAZY) // Many tasks can belong to one todolist
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public TodoList(){}

    public TodoList(String name, List<Task> tasks, User user) {
        this.name = name;
        this.tasks=tasks;
        this.user=user;

    }
    public TodoList(Long id,String name, List<Task> tasks, User user){
        this.id=id;
        this.name=name;
        this.tasks=tasks;
        this.user=user;
    }



}
