package project.Todolist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Todolist.Repository.TaskRepository;
import project.Todolist.Repository.TodoListRepository;
import project.Todolist.api.email.reminder.ReminderEmailService;
import project.Todolist.api.model.Task;
import project.Todolist.api.model.TodoList;
import project.Todolist.api.model.User;
import project.Todolist.api.user.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TodoListRepository todoListRepository;

    private final ReminderEmailService reminderEmailService;
    private  final UserRepository userRepository;

public Task createTaskInList(Task task, TodoList todoList, User user) {
    task.setUser(user);
    task.setTodoList(todoList);
    Task createdTask = taskRepository.save(task);
    return createdTask;
}
    public Task createTask(Task task, User user) {
    task.setUser(user);
        return taskRepository.save(task);

    }

    public Task editTask(Long taskId,TodoList todoList) {
        return todoList.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));


    }


    public void updatedTask(Long taskId, Task taskUpdateRequest) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        existingTask.setTitle(taskUpdateRequest.getTitle());
        existingTask.setDescription(taskUpdateRequest.getDescription());
        existingTask.setDueDate(taskUpdateRequest.getDueDate());
        existingTask.setDueTime(taskUpdateRequest.getDueTime());
        existingTask.setReminder(taskUpdateRequest.getReminder());
        existingTask.setPriority(taskUpdateRequest.getPriority());

        taskRepository.save(existingTask);
    }

public List<Task> getAllTasksWithoutListId(Long user_id) {
    User user = userRepository.findById(user_id)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

    return taskRepository.findAllByTodoListIsNull().stream()
            .filter(task -> task.getUser() != null && task.getUser().equals(user) && !task.isArchived())
            .collect(Collectors.toList());
}



    public void updateTask(Task task) {
        taskRepository.save(task);
    }



    public void deleteTask(Long  taskId,Long todoListId){
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));
        Task taskToDelete = todoList.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));
        todoList.getTasks().remove(taskToDelete);
        taskRepository.delete(taskToDelete);
    }


    public void deleteAllTask(Long todoListId){
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));
        List<Task> tasks = todoList.getTasks();
        todoList.getTasks().removeAll(tasks);
        {
            taskRepository.deleteAll(tasks);
        }

    }

public Task getTaskById(Long taskId) {
    return taskRepository.findById(taskId)
            .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " not found"));
}

    public void markTaskAsCompleted(Long taskId, TodoList todoList, boolean completed) {
        Task task = todoList.getTasks().stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));

        task.setCompleted(completed);
        taskRepository.save(task);
    }



    public List<Task> getCompletedTasks(boolean isCompleted) {
        List<TodoList> allTodoLists = todoListRepository.findAll();
        return allTodoLists.stream()
                .flatMap(todoList -> todoList.getTasks().stream())
                .filter(task -> task.isCompleted() == isCompleted  && !task.isArchived())
                .collect(Collectors.toList());
    }

    public void markTaskAsCompletedWithoutListId(Long taskId, boolean completed) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " not found"));

        task.setCompleted(completed);
        taskRepository.save(task);
    }
    public List<Task> getAllTasksInTodoList( Long user_id, Long todoListId) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));

        return todoList.getTasks().stream()
                .filter(task -> !task.isArchived())
                .collect(Collectors.toList());
    }

public List<Task> filterTasksByOverdue(Long user_id) {
    User user = userRepository.findById(user_id)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

    LocalDate today = LocalDate.now();

    return taskRepository.findAll().stream()
            .filter(task -> (task.getUser() != null && task.getUser().equals(user))
                    &&(task.getDueDate() != null && task.getDueDate().isBefore(today) && !task.isArchived() && !task.isCompleted()))
            .collect(Collectors.toList());
}


public List<Task> filterTasksByToday(Long user_id) {
    User user = userRepository.findById(user_id)
            .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

    LocalDate today = LocalDate.now();

    return taskRepository.findAll().stream()
            .filter(task -> task.getUser() != null && task.getUser().equals(user) && task.getDueDate() != null && task.getDueDate().isEqual(today) && !task.isArchived() && !task.isCompleted())
            .collect(Collectors.toList());
}

    public List<Task> filterTasksByUpcoming(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAfter = today.plusWeeks(1).plusDays(1);
        return taskRepository.findAll().stream()
                .filter(task -> (task.getUser() != null && task.getUser().equals(user))&& task.getDueDate() != null && task.getDueDate().isAfter(today) && task.getDueDate().isBefore(oneWeekAfter) && !task.isArchived() && !task.isCompleted())
                .collect(Collectors.toList());
    }


    public List<Task> getArchivedTasks(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        return taskRepository.findByUserAndArchived(user, true);
    }

    public List<Task> getNonArchivedTasks(Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        return taskRepository.findByUserAndArchived(user, false);
    }

        public List<Task> getTasksSortedByPriority(Long todoListId) {
            List<Task> tasks = getNonArchivedTasks(todoListId);
            tasks.sort(
                    Comparator.comparing(Task::getPriority)
                            .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
            );
            return tasks;
        }


    public List<Task> getTasksSortedByDueDate(Long todoListId) {
        List<Task> tasks = getNonArchivedTasks(todoListId);
        tasks.sort(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())));
        return tasks;
    }

    public List<Task> getTasksSortedByTitle(Long todoListId) {
        List<Task> tasks = getNonArchivedTasks(todoListId);
        tasks.sort(Comparator.comparing(Task::getTitle));
        return tasks;
    }

    public List<Task> getTasksSortedById(Long todoListId) {
        List<Task> tasks = getNonArchivedTasks(todoListId);
        tasks.sort(Comparator.comparing(Task::getId, Comparator.nullsLast(Comparator.naturalOrder())));
        return tasks;
    }



    public List<Task> searchTask(Long todoListId, String title) {
    TodoList todoList = todoListRepository.findById(todoListId)
            .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));

    List<Task> tasks = todoList.getTasks();
    List<Task> matchingTasks = new ArrayList<>();

    for (Task task : tasks) {
        if (task.getTitle().contains(title)) {
            matchingTasks.add(task);
        }
    }

    if (matchingTasks.isEmpty()) {
        throw new EntityNotFoundException("No tasks found with the letter '" + title + "' in TodoList with ID " + todoListId);
    }

    return matchingTasks;
}

    public void archiveTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " not found"));

        task.setArchived(true);
        taskRepository.save(task);
    }
    public void unArchiveTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " not found"));

        task.setArchived(false);
        taskRepository.save(task);
    }
    public List<Task> getArchivedTasks() {
        return taskRepository.findByArchived(true);
    }


    public List<Task> sortTasksByTitle(List<Task> tasks) {

        tasks.sort(Comparator.comparing(Task::getTitle));
        return tasks;
    }

    public List<Task> sortTasksByPriority(List<Task> tasks) {
        tasks.sort(
                Comparator.comparing(Task::getPriority)
                        .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
        );
        return tasks;
    }

        public List<Task> sortTasksByDueDate(List<Task> tasks) {
            tasks.sort(Comparator.comparing(task -> {
                LocalDate dueDate = task.getDueDate();
                return (dueDate != null) ? dueDate : LocalDate.MAX;
            }));
            return tasks;
        }


    public void deleteTaskById(Long taskId) {
        Task taskToDelete = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with ID: " + taskId));
        TodoList todoList = taskToDelete.getTodoList();
        if (todoList != null) {
            todoList.getTasks().remove(taskToDelete);
            todoListRepository.save(todoList);
        }

        taskRepository.delete(taskToDelete);
    }
    public long getArchivedTaskCount(User user) {
        List<Task> archivedTasks = taskRepository.findAllByUserAndArchived(user, true);
        return archivedTasks.size();
    }

    public long getOverdueTaskCount(User user) {
        LocalDate today = LocalDate.now();
        List<Task> overdueTasks = taskRepository.findAllByUserAndDueDateBeforeAndArchivedAndCompleted(user, today, false, false);
        return overdueTasks.size();
    }
    public long countTasksDueToday(User user) {

        LocalDate today = LocalDate.now();

        List<Task> todayTasks=  taskRepository.findAllByUserAndDueDateAndArchivedAndCompleted(user, today, false, false);
        return todayTasks.size();
    }

    public long countUpcomingTasks(User user) {

        LocalDate today = LocalDate.now().plusDays(1);;
        LocalDate oneWeekAfter = today.plusWeeks(1).plusDays(1);
        List<Task> upcomingTasks = taskRepository.findAllByUserAndDueDateBetweenAndArchivedAndCompleted(user, today, oneWeekAfter, false, false);
        return upcomingTasks.size();
    }
    public  long countInbox(User user){
    List <Task> inbox= taskRepository.findAllByUserAndTodoListIsNullAndArchived(user,false);
    return  inbox.size();
    }



}
