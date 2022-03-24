package com.devo.jwt.controllers;

import com.devo.jwt.models.Task;
import com.devo.jwt.repositories.TaskRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "List tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping()
    List<Task> all(){
        return taskRepository.findAll();
    }

    @PostMapping()
    ResponseEntity<Task> add(@RequestBody Task task) {

        Task savedTask = taskRepository.save(task);
        return ResponseEntity.created(URI.create(String.format("/tasks/%s", savedTask.getId()))).build();
    }

}
