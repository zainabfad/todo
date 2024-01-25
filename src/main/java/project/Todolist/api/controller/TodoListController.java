package project.Todolist.api.controller;

import javax.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import project.Todolist.api.user.UserRepository;
import project.Todolist.api.model.Task;
import project.Todolist.api.model.TodoList;
import project.Todolist.api.model.User;
import project.Todolist.service.TodoListService;

;
import java.util.List;

@RequestMapping( "/api/v1/list")
@RestController
@Slf4j
@Transactional
public class TodoListController {
    @Autowired
    private final TodoListService todoListService;
    @Autowired
    private final UserRepository userRepository;
    public TodoListController(TodoListService todoListService,UserRepository userRepository){
        this.todoListService=todoListService;
        this.userRepository=userRepository;

    }

    @PostMapping("/{user_id}")
    public TodoList createLists(@RequestBody TodoList todoList, @PathVariable Long user_id){
        log.info("Received TodoList: {}", todoList);
        System.out.println("Received TodoList: " + todoList);
        User user=userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

        return  todoListService.addNewList(todoList,user);
    }
    @DeleteMapping("/{user_id}/{todoListId}")
    public ResponseEntity<Object> deleteList(@PathVariable Long user_id,
                                             @PathVariable Long todoListId){
        try {
            todoListService.deleteList(todoListId,user_id);
            return ResponseEntity.status(HttpStatus.OK).body("list deleted successfully");
        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This list Id " +  todoListId  +" doesn't exist");
        }

    }
    @PutMapping("/{user_id}/{todoListId}")
    public ResponseEntity<Object> editTask(@PathVariable Long user_id,
                                           @PathVariable Long todoListId,
                                           @RequestBody TodoList updatedList) {
        try {
            User user=userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
            TodoList existingList=todoListService.editList(todoListId,user);
            existingList.setName(updatedList.getName());
            todoListService.updatedList(existingList);

            return ResponseEntity.status(HttpStatus.OK).body("List with ID " + todoListId + " updated successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List not found with ID: " + todoListId);
        }
    }
    @GetMapping("/all/{user_id}")
    public ResponseEntity<Object> getAllList(@PathVariable Long user_id){
        try {
            List<TodoList> todoLists = todoListService.getAllList(user_id);
            return ResponseEntity.status(HttpStatus.OK).body(todoLists);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/{user_id}/{todoListId}")
    public ResponseEntity<Object> getListById(@PathVariable Long user_id,
                                              @PathVariable Long todoListId){
        try{
            User user=userRepository.findById(user_id)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
            TodoList specificList =todoListService.getListById(todoListId,user);
            if(specificList != null){
                return ResponseEntity.status(HttpStatus.OK).body(specificList);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List doesn't exist");
            }
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("List doesn't exist");
        }
    }

}
