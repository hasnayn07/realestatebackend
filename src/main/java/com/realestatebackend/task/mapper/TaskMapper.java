package com.realestatebackend.task.mapper;

import com.realestatebackend.task.dto.TaskRequest;
import com.realestatebackend.task.dto.TaskResponse;
import com.realestatebackend.task.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedTo", ignore = true) // We will fetch the User entity manually in the service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toEntity(TaskRequest request);

    @Mapping(source = "assignedTo.id", target = "assignedToId")
    @Mapping(source = "assignedTo.fullName", target = "assignedToName") // Updated here
    TaskResponse toResponse(Task task);
}