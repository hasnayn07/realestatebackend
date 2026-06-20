package com.realestatebackend.task.repository;

import com.realestatebackend.task.entity.Task;
import com.realestatebackend.task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Fetch tasks assigned to a specific agent
    List<Task> findByAssignedTo_Id(UUID agentId);

    // Filter tasks assigned to an agent by their active status (e.g., PENDING, IN_PROGRESS)
    List<Task> findByAssignedTo_IdAndStatus(UUID agentId, TaskStatus status);

    // Today's Action Items: Due today and not completed/cancelled
    List<Task> findByAssignedTo_IdAndDueDateAndStatusNotIn(UUID agentId, LocalDate date, List<TaskStatus> inactiveStatuses);

    // Overdue Follow-ups: Past due date and still pending/in progress
    List<Task> findByAssignedTo_IdAndDueDateBeforeAndStatusIn(UUID agentId, LocalDate date, List<TaskStatus> activeStatuses);

    // Manager View: System-wide overview of all uncompleted past-due follow-ups
    List<Task> findByDueDateBeforeAndStatusIn(LocalDate date, List<TaskStatus> activeStatuses);
}