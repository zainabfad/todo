package project.Todolist.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.Todolist.Repository.TodoListRepository;
import project.Todolist.api.user.UserRepository;
import project.Todolist.api.model.TodoList;
import project.Todolist.api.model.User;

import javax.persistence.EntityNotFoundException;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class TodoListService {
    private final TodoListRepository todoListRepository;
    private final UserRepository userRepository;


    public TodoList addNewList(TodoList todoList, User user){
        todoList.setUser(user);
        return  todoListRepository.save(todoList);
    }

    public void deleteList(Long  todoListId, Long user_id){
        User user=userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));

        TodoList listToDelete=user.getTodoLists().stream()
                .filter(todoList -> todoList.getId().equals(todoListId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("List not found with ID: " + todoListId));;
        user.getTodoLists().remove(listToDelete);
        todoListRepository.delete(listToDelete);
    }
    public TodoList editList(Long todoListId,User user) {
        return user.getTodoLists().stream()
                .filter(todoList -> todoList.getId().equals(todoListId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("List not found with ID: " + todoListId));

    }

    public void updatedList(TodoList todoList){
        todoListRepository.save(todoList);
    }

    public List<TodoList> getAllList(Long user_id){
        User user=userRepository.findById(user_id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + user_id + " not found"));
        return user.getTodoLists();
    }
    public TodoList getListById(Long todoListId, User user){
        return user.getTodoLists().stream()
                .filter(task -> task.getId().equals(todoListId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("List not found with ID: " + todoListId));
//        return  todoListRepository.findById(todoListId)
//                .orElseThrow(() -> new EntityNotFoundException("List doesn't exists"));
    }

//        public TodoList addNewList(TodoList todoList) {
//            return todoListRepository.save(todoList);
//        }
//
//        public void deleteList(Long todoListId) {
//            TodoList listToDelete = getListById(todoListId);
//            todoListRepository.delete(listToDelete);
//        }
//
//        public TodoList editList(Long todoListId) {
//        return getListById(todoListId);
//    }
//
//
//        public void updateList(TodoList todoList) {
//            todoListRepository.save(todoList);
//        }
//
//        public List<TodoList> getAllLists() {
//            return todoListRepository.findAll();
//        }
//
//        public TodoList getListById(Long todoListId) {
//            return todoListRepository.findById(todoListId)
//                    .orElseThrow(() -> new EntityNotFoundException("List not found with ID: " + todoListId));
//        }









}
