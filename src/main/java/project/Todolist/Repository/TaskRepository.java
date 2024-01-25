package project.Todolist.Repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import project.Todolist.api.model.Task;
import project.Todolist.api.model.TodoList;
import project.Todolist.api.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCompleted(boolean isCompleted);

    List<Task> findAllByReminderSent(boolean reminderSent);
    boolean existsByReminderSentAndId(boolean reminderSent, Long taskId);

    List<Task> findByArchived(boolean archived);

    List<Task> findAllByTodoListIsNull();

    List<Task> findByUserAndArchived(User user, boolean b);
    List <Task> findAllByUserAndArchived(User user, boolean b);
    List<Task> findAllByUserAndDueDateBeforeAndArchivedAndCompleted(User user, LocalDate dueDate, boolean archived, boolean completed);

    List<Task> findAllByUserAndDueDateAndArchivedAndCompleted(User user, LocalDate today, boolean archived, boolean completed);

    List <Task>findAllByUserAndDueDateBetweenAndArchivedAndCompleted(User user, LocalDate today, LocalDate oneWeekAfter, boolean b, boolean b1);

    List<Task> findAllByUserAndTodoListIsNullAndArchived(User user, boolean b);
}
