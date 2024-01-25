package project.Todolist.api.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import project.Todolist.Repository.InboxRepository;
import project.Todolist.Repository.TodoListRepository;
import project.Todolist.api.model.Inbox;
import project.Todolist.api.model.Task;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import project.Todolist.api.model.TodoList;
import project.Todolist.configuration.AESGCMNoPadding;
import project.Todolist.service.InboxService;
import project.Todolist.service.TaskService;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RequestMapping("/api/v1/inbox")
@RestController
@Slf4j
@RequiredArgsConstructor
public class InboxController {
    @Autowired
    private final InboxService inboxService;

    @PostMapping
    public Inbox createTask(@RequestBody Inbox inbox) {

        return inboxService.createInbox(inbox);
    }
}
