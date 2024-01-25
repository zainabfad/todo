package project.Todolist.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import project.Todolist.Repository.TodoListRepository;
import project.Todolist.api.model.Task;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import project.Todolist.api.model.TodoList;
import project.Todolist.api.model.User;
import project.Todolist.api.user.UserRepository;
import project.Todolist.configuration.AESGCMNoPadding;
import project.Todolist.service.TaskService;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RequestMapping("/api/v1/task")
@RestController
@Slf4j
@RequiredArgsConstructor
public class TaskController {

    @Autowired
    private final TaskService taskService;
    @Autowired
    private AESGCMNoPadding encryptUtil;
    @Autowired
    private  final UserRepository userRepository;
    @Autowired
    private final TodoListRepository todoListRepository;


    @PostMapping("{user_id}/{todoListId}")
    public Task createTaskInList(@RequestBody Task task, @PathVariable Long todoListId , @PathVariable Long user_id) {
        User user=userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        TodoList todoList = todoListRepository.findById(todoListId)
                .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));

        return taskService.createTaskInList(task, todoList, user);
    }

    @PostMapping("{user_id}")
    public Task createTask(@RequestBody Task task, @PathVariable Long user_id) {
        User user=userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        return taskService.createTask(task, user);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Object> updateTask(@PathVariable Long taskId, @RequestBody Task taskUpdateRequest) {
        try {
            taskService.updatedTask(taskId, taskUpdateRequest);
            return ResponseEntity.ok("Task updated successfully" + taskUpdateRequest);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid task ID");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


    @GetMapping("/filter/overdue/{user_id}")
    public ResponseEntity<Object> filterTasksByOverdue(@PathVariable Long user_id) {
        try {
            List<Task> tasks = taskService.filterTasksByOverdue(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/filter/today/{user_id}")
    public ResponseEntity<Object> filterTasksByToday(@PathVariable Long user_id) {
        try {
            List<Task> tasks = taskService.filterTasksByToday(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/filter/upcoming/{user_id}")
    public ResponseEntity<Object> filterTasksByUpcoming(@PathVariable Long user_id) {
        try {
            List<Task> tasks = taskService.filterTasksByUpcoming(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

//    @GetMapping(value = "/filter/upcoming", produces = MediaType.TEXT_PLAIN_VALUE)
//    public ResponseEntity<String> filterTasksByUpcomingEnc() {
//        try {
//            List<Task> tasks = taskService.filterTasksByUpcoming();
//            return ResponseEntity.status(HttpStatus.OK).body(encryptUtil.encrypt(tasks.toString()));
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }

    @GetMapping("/sorted/{todoListId}")
    public ResponseEntity<Object> getSortedTasks(@PathVariable Long todoListId,
                                                 @RequestParam(value = "sort", defaultValue = "id") String sort) {
        try {
            List<Task> tasks;

            if ("title".equalsIgnoreCase(sort)) {
                tasks = taskService.getTasksSortedByTitle(todoListId);
            } else if ("dueDate".equalsIgnoreCase(sort)) {
                tasks = taskService.getTasksSortedByDueDate(todoListId);
            } else if ("priority".equalsIgnoreCase(sort)) {
                tasks = taskService.getTasksSortedByPriority(todoListId);
            } else if ("id".equalsIgnoreCase(sort)) {
                tasks = taskService.getTasksSortedById(todoListId);
            } else {
                tasks=taskService.getTasksSortedById(todoListId);
            }

            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @PutMapping("/{todoListId}/{taskId}")
    public ResponseEntity<Object> editTask(@PathVariable Long todoListId,
                                           @PathVariable Long taskId,
                                           @RequestBody Task updatedTask) {
        try {
            TodoList todoList = todoListRepository.findById(todoListId)
                    .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));

            Task existingTask = taskService.editTask(taskId, todoList);
            if(updatedTask.getTitle()!=null) {
                existingTask.setTitle(updatedTask.getTitle());
            }else {
                throw new IllegalStateException("please enter a title");
            }
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setDueDate(updatedTask.getDueDate());
            existingTask.setDueTime(updatedTask.getDueTime());
            existingTask.setPriority(updatedTask.getPriority());
            existingTask.setReminder(updatedTask.getReminder());


            taskService.updateTask(existingTask);
            return ResponseEntity.status(HttpStatus.OK).body("Task with ID " + taskId + " updated successfully\n"+ updatedTask);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }




    @GetMapping("/all/{user_id}/{todoListId}")
    public ResponseEntity<?> getAllTasksInTodoList(
            @PathVariable Long user_id,
            @PathVariable Long todoListId) {
        try {

            List<Task> tasks = taskService.getAllTasksInTodoList(user_id,todoListId);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/all/{user_id}")
    public ResponseEntity<?> getAllTasksWithoutListId(@PathVariable Long user_id) {
        try {
            List<Task> tasks = taskService.getAllTasksWithoutListId(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{todoListId}/{taskId}")
    public ResponseEntity<Object> deleteTask(@PathVariable Long todoListId,
                                             @PathVariable Long taskId){
        try {
            taskService.deleteTask(taskId, todoListId);
            return ResponseEntity.status(HttpStatus.OK).body("Task deleted successfully");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTaskById(@PathVariable Long taskId) {
        try {
            taskService.deleteTaskById(taskId);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting task");
        }
    }
    @DeleteMapping("/all/{todoListId}")
    public ResponseEntity<Object>deleteAllTask(@PathVariable Long todoListId){
        try{
            taskService.deleteAllTask(todoListId);
            return ResponseEntity.status(HttpStatus.OK).body("All tasks have deleted");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

@GetMapping("/{taskId}")
public ResponseEntity<Object> getTaskById(@PathVariable Long taskId) {
    try {
        Task specificTask = taskService.getTaskById(taskId);

        if (specificTask != null) {
            return ResponseEntity.status(HttpStatus.OK).body(specificTask);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task doesn't exist");
        }
    } catch (EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

    @GetMapping("/completed/{todoListId}/{taskId}")
    public ResponseEntity<Object> markTaskAsCompleted(
            @PathVariable Long todoListId,
            @PathVariable Long taskId,
            @RequestParam("completed") boolean completed) {
        try {
            TodoList todoList = todoListRepository.findById(todoListId)
                    .orElseThrow(() -> new EntityNotFoundException("TodoList with ID " + todoListId + " not found"));
            taskService.markTaskAsCompleted(taskId, todoList, completed);

            String completionStatus = completed ? "true" : "false";
            return ResponseEntity.status(HttpStatus.OK).body("Task with ID " + taskId + " marked as " + completionStatus + ".");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/completed/{taskId}")
    public ResponseEntity<Object> markTaskAsCompleted(
            @PathVariable Long taskId,
            @RequestParam("completed") boolean completed) {
        try {
            // Assuming your service method handles tasks without a list ID
            taskService.markTaskAsCompletedWithoutListId(taskId, completed);

            String completionStatus = completed ? "true" : "false";
            return ResponseEntity.status(HttpStatus.OK).body("Task with ID " + taskId + " marked as " + completionStatus + ".");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }




    @GetMapping("/search/{todoListId}")
    public ResponseEntity<Object> searchTask(
            @PathVariable Long todoListId,
            @RequestParam("search") String title) {
        try {
            List<Task> matchingTasks = taskService.searchTask(todoListId, title);
            return ResponseEntity.status(HttpStatus.OK).body(matchingTasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/archive/{taskId}")
    public ResponseEntity<Object> archiveTask(@PathVariable Long taskId) {
        try {
            taskService.archiveTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body("Task archived successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/unarchive/{taskId}")
    public ResponseEntity<Object> UnArchiveTask(@PathVariable Long taskId) {
        try {
            taskService.unArchiveTask(taskId);
            return ResponseEntity.status(HttpStatus.OK).body("Task Unarchived successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/archive/{user_id}")
    public ResponseEntity<Object> getArchivedTasks(@PathVariable Long user_id) {
        try {
            List<Task> archivedTasks = taskService.getArchivedTasks(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(archivedTasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    @GetMapping("/filter/today/{user_id}/sorted")
    public ResponseEntity<Object> filterAndSortTasksByToday(
            @PathVariable Long user_id,
            @RequestParam(value = "sort", defaultValue = "id") String sort) {
        try {
            List<Task> tasks;
            List<Task> todayTasks = taskService.filterTasksByToday(user_id);

            if ("title".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByTitle(todayTasks);
            } else if ("priority".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByPriority(todayTasks);
            } else {
                tasks = todayTasks;
            }

            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/filter/overdue/{user_id}/sorted")
    public ResponseEntity<Object> filterAndSortOverdueTasks(
            @PathVariable Long user_id,
            @RequestParam(value = "sort", defaultValue = "id") String sort) {
        try {
            List<Task> tasks;
            List<Task> overdueTasks = taskService.filterTasksByOverdue(user_id);

            if ("title".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByTitle(overdueTasks);
            } else if ("priority".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByPriority(overdueTasks);
            } else if ("dueDate".equalsIgnoreCase(sort)) {
                tasks=taskService.sortTasksByDueDate(overdueTasks);
            } else {
                tasks = overdueTasks;
            }

            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/filter/archived/{user_id}/sorted")
    public ResponseEntity<Object> filterAndSortArchiveTasks(
            @RequestParam(value = "sort", defaultValue = "id") String sort) {
        try {
            List<Task> tasks;
            List<Task> archivedTasks = taskService.getArchivedTasks();

            if ("title".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByTitle(archivedTasks);
            } else if ("priority".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByPriority(archivedTasks);
            } else if ("dueDate".equalsIgnoreCase(sort)) {
                tasks=taskService.sortTasksByDueDate(archivedTasks);
            } else {
                tasks = archivedTasks;
            }

            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/filter/not-listed/{user_id}/sorted")
    public ResponseEntity<Object> filterAndSortNotListedTasks(
            @PathVariable Long user_id,
            @RequestParam(value = "sort", defaultValue = "id") String sort) {
        try {
            List<Task> tasks;
            List<Task> notListedTasks = taskService.getAllTasksWithoutListId(user_id);

            if ("title".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByTitle(notListedTasks);
            } else if ("priority".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByPriority(notListedTasks);
            } else if ("dueDate".equalsIgnoreCase(sort)) {
                tasks = taskService.sortTasksByDueDate(notListedTasks);
            } else {
                tasks = notListedTasks;
            }

            return ResponseEntity.status(HttpStatus.OK).body(tasks);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/archive/count/{user_id}")
    public ResponseEntity<Object> getArchivedTaskCount(@PathVariable Long user_id) {
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

            long archivedTaskCount = taskService.getArchivedTaskCount(user);
            return ResponseEntity.status(HttpStatus.OK).body(archivedTaskCount);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/overdue/count/{user_id}")
    public ResponseEntity<Object> getOverdueTaskCount(@PathVariable Long user_id) {
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

            long overdueTaskCount = taskService.getOverdueTaskCount(user);
            return ResponseEntity.status(HttpStatus.OK).body(overdueTaskCount);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/today/count/{user_id}")
    public ResponseEntity<Object> countTasksDueToday(@PathVariable Long user_id) {
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

            long count = taskService.countTasksDueToday(user);
            return ResponseEntity.status(HttpStatus.OK).body(count);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/upcoming/count/{user_id}")
    public ResponseEntity<Object> countUpcomingTasks(@PathVariable Long user_id) {
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
            long count = taskService.countUpcomingTasks(user);
            return ResponseEntity.status(HttpStatus.OK).body(count);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/inbox/count/{user_id}")
    public ResponseEntity<Object> countInbox(@PathVariable Long user_id) {
        try {
            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
            long count = taskService.countInbox(user);
            return ResponseEntity.status(HttpStatus.OK).body(count);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
