package com.realestatebackend.task.service.impl;

import com.realestatebackend.audit.entity.AuditAction;
import com.realestatebackend.audit.service.AuditLogService;
import com.realestatebackend.auth.entity.User;
import com.realestatebackend.auth.repository.UserRepository;
import com.realestatebackend.common.exception.NotFoundException;
import com.realestatebackend.task.dto.TaskRequest;
import com.realestatebackend.task.dto.TaskResponse;
import com.realestatebackend.task.entity.Task;
import com.realestatebackend.task.entity.TaskStatus;
import com.realestatebackend.task.mapper.TaskMapper;
import com.realestatebackend.task.repository.TaskRepository;
import com.realestatebackend.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final AuditLogService auditLogService;


    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        // 1. Safely resolve the assigned User (Agent)
        User agent = userRepository.findById(request.assignedToId())
                .orElseThrow(() -> new NotFoundException("Agent not found with ID: " + request.assignedToId()));

        // 2. Map and link
        Task task = taskMapper.toEntity(request);
        task.setAssignedTo(agent);

        task = taskRepository.save(task);
        log.info("Created task '{}' assigned to {}", task.getTitle(), agent.getFullName());

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with ID: " + id));
        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse updateTaskStatus(UUID id, TaskStatus newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with ID: " + id));

        // 1. THIS IS THE MISSING LINE! Capture the old status before changing it
        TaskStatus oldStatus = task.getStatus();

        // 2. Now update the task to the new status
        task.setStatus(newStatus);

        log.info("Task '{}' status updated to {}", task.getTitle(), newStatus);

        // 3. Now Java knows exactly what 'oldStatus' is for the description
        String description = String.format("Changed task status from %s to %s", oldStatus, newStatus);

        auditLogService.logAction(
                AuditAction.STATUS_CHANGE,
                "TASK",
                task.getId(),
                description
        );

        return taskMapper.toResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForAgent(UUID agentId) {
        return taskRepository.findByAssignedTo_Id(agentId).stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTodaysTasksForAgent(UUID agentId) {
        LocalDate today = LocalDate.now();
        List<TaskStatus> inactiveStatuses = List.of(TaskStatus.COMPLETED, TaskStatus.CANCELLED);

        return taskRepository.findByAssignedTo_IdAndDueDateAndStatusNotIn(agentId, today, inactiveStatuses)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasksForAgent(UUID agentId) {
        LocalDate today = LocalDate.now();
        List<TaskStatus> activeStatuses = List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS);

        return taskRepository.findByAssignedTo_IdAndDueDateBeforeAndStatusIn(agentId, today, activeStatuses)
                .stream()
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllOverdueTasks() {
        LocalDate today = LocalDate.now();
        List<TaskStatus> activeStatuses = List.of(TaskStatus.PENDING, TaskStatus.IN_PROGRESS);

        return taskRepository.findByDueDateBeforeAndStatusIn(today, activeStatuses).stream()
                .map(taskMapper::toResponse)
                .toList();
    }
}