package com.realestatebackend.task.controller;

import com.realestatebackend.task.dto.TaskRequest;
import com.realestatebackend.task.dto.TaskResponse;
import com.realestatebackend.task.entity.TaskStatus;
import com.realestatebackend.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'AGENT')")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    // Lightweight endpoint strictly for updating the state machine of the task
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, status));
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<TaskResponse>> getTasksForAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(taskService.getTasksForAgent(agentId));
    }

    @GetMapping("/agent/{agentId}/today")
    public ResponseEntity<List<TaskResponse>> getTodaysTasksForAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(taskService.getTodaysTasksForAgent(agentId));
    }

    @GetMapping("/agent/{agentId}/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasksForAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(taskService.getOverdueTasksForAgent(agentId));
    }

    // High-level aggregation locked down to management only
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TaskResponse>> getAllOverdueTasks() {
        return ResponseEntity.ok(taskService.getAllOverdueTasks());
    }
}