package com.realestatebackend.task.service;

import com.realestatebackend.task.dto.TaskRequest;
import com.realestatebackend.task.dto.TaskResponse;
import com.realestatebackend.task.entity.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    TaskResponse createTask(TaskRequest request);
    TaskResponse getTaskById(UUID id);
    TaskResponse updateTaskStatus(UUID id, TaskStatus newStatus);

    List<TaskResponse> getTasksForAgent(UUID agentId);
    List<TaskResponse> getTodaysTasksForAgent(UUID agentId);
    List<TaskResponse> getOverdueTasksForAgent(UUID agentId);
    List<TaskResponse> getAllOverdueTasks(); // For the Manager/Admin view
}