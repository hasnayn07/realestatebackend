package com.realestatebackend.booking.service.impl;

import com.realestatebackend.booking.dto.InstallmentResponse;
import com.realestatebackend.booking.entity.Installment;
import com.realestatebackend.booking.repository.InstallmentRepository;
import com.realestatebackend.booking.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstallmentServiceImpl implements InstallmentService {

    private final InstallmentRepository installmentRepo;

    @Override
    @Transactional(readOnly = true)
    public List<InstallmentResponse> getByBooking(UUID bookingId) {
        return installmentRepo.findByBooking_IdOrderByInstallmentNumberAsc(bookingId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstallmentResponse> getOverdue() {
        return installmentRepo.findOverdue(LocalDate.now())
                .stream().map(this::toResponse).toList();
    }

    private InstallmentResponse toResponse(Installment i) {
        return new InstallmentResponse(
                i.getId(), i.getBooking().getId(), i.getInstallmentNumber(),
                i.getDueDate(), i.getAmountDue(), i.getAmountPaid(),
                i.getAmountDue().subtract(i.getAmountPaid()), i.getStatus());
    }
}