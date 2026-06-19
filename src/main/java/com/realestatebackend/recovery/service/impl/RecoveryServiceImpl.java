package com.realestatebackend.recovery.service.impl;

import com.realestatebackend.booking.entity.Installment;
import com.realestatebackend.booking.entity.InstallmentStatus;
import com.realestatebackend.booking.repository.InstallmentRepository;
import com.realestatebackend.booking.repository.PaymentRepository;
import com.realestatebackend.recovery.dto.DefaulterDto;
import com.realestatebackend.recovery.dto.RecoveryDashboardResponse;
import com.realestatebackend.recovery.service.RecoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecoveryServiceImpl implements RecoveryService {

    private final InstallmentRepository installmentRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional(readOnly = true)
    public RecoveryDashboardResponse getDashboardMetrics() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        BigDecimal totalOutstanding = installmentRepository.totalOutstandingOverall();
        BigDecimal totalOverdue = installmentRepository.getTotalOverdueAmount();
        BigDecimal dueThisMonth = installmentRepository.getDueThisMonthAmount(year, month);
        BigDecimal collectedThisMonth = paymentRepository.getCollectedThisMonthAmount(year, month);

        BigDecimal recoveryRate = calculateRecoveryRate(dueThisMonth, collectedThisMonth);

        // Fetch top 10 defaulters using pagination
        List<DefaulterDto> defaulters = installmentRepository.findTopDefaulters(PageRequest.of(0, 10));

        return new RecoveryDashboardResponse(
                totalOutstanding,
                totalOverdue,
                dueThisMonth,
                collectedThisMonth,
                recoveryRate,
                defaulters
        );
    }

    private BigDecimal calculateRecoveryRate(BigDecimal dueThisMonth, BigDecimal collectedThisMonth) {
        if (dueThisMonth.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        // Formula: (collected / due) * 100
        return collectedThisMonth.multiply(new BigDecimal("100"))
                .divide(dueThisMonth, 2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 1 * * ?") // Runs at 1:00 AM every day
    public void sweepAndMarkOverdueInstallments() {
        LocalDate today = LocalDate.now();
        List<Installment> pastDueInstallments = installmentRepository.findOverdue(today);

        int updatedCount = 0;
        for (Installment installment : pastDueInstallments) {
            if (installment.getStatus() == InstallmentStatus.PENDING) {
                installment.setStatus(InstallmentStatus.OVERDUE);
                updatedCount++;
            }
        }

        // Because of @Transactional, Hibernate's dirty checking will automatically
        // issue the UPDATE statements for any entities we modified.
        log.info("Daily Recovery Sweep completed. Marked {} installments as OVERDUE.", updatedCount);
    }
}