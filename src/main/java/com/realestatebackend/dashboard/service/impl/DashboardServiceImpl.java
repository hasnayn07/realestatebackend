package com.realestatebackend.dashboard.service.impl;

import com.realestatebackend.dashboard.dto.DashboardKpiResponse;
import com.realestatebackend.dashboard.service.DashboardService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.realestatebackend.dashboard.dto.MonthlyRevenueDTO;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public DashboardKpiResponse getTopLevelKpis() {
        log.info("Calculating Command Center KPIs...");

        // 1. Total Inventory Count
        Long totalUnits = entityManager.createQuery(
                        "SELECT COUNT(u) FROM Unit u", Long.class)
                .getSingleResult();

        // 2. Available Units
        Long availableUnits = entityManager.createQuery(
                        "SELECT COUNT(u) FROM Unit u WHERE u.status = 'AVAILABLE'", Long.class)
                .getSingleResult();

        // 3. Total Expected Revenue (Sum of salePrice from ACTIVE bookings)
        BigDecimal expectedRevenue = entityManager.createQuery(
                        "SELECT SUM(b.salePrice) FROM Booking b WHERE b.status != 'CANCELLED'", BigDecimal.class)
                .getSingleResult();

        // 4. Total Cash Collected (Sum of amountPaid from installments)
        // Corrected to use 'amountPaid' from your Installment entity
        BigDecimal cashCollected = entityManager.createQuery(
                        "SELECT SUM(i.amountPaid) FROM Installment i WHERE i.status = 'PAID'", BigDecimal.class)
                .getSingleResult();

        // 5. Active Defaulters
        Long activeDefaulters = entityManager.createQuery(
                        "SELECT COUNT(i) FROM Installment i WHERE i.status = 'OVERDUE'", Long.class)
                .getSingleResult();

        // Safe null handling
        expectedRevenue = expectedRevenue != null ? expectedRevenue : BigDecimal.ZERO;
        cashCollected = cashCollected != null ? cashCollected : BigDecimal.ZERO;

        return new DashboardKpiResponse(
                totalUnits,
                availableUnits,
                expectedRevenue,
                cashCollected,
                activeDefaulters
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyRevenueDTO> getMonthlyRevenueTrend() {
        // We use the Java Entity class 'Installment' here, not the SQL table 'installments'
        // We also use 'i.dueDate' and 'i.amountPaid' as defined in your Installment.java
        List<Object[]> results = entityManager.createQuery(
                        "SELECT FUNCTION('DATE_FORMAT', i.dueDate, '%Y-%m'), SUM(i.amountPaid) " +
                                "FROM Installment i " +
                                "WHERE i.status = 'PAID' " +
                                "GROUP BY FUNCTION('DATE_FORMAT', i.dueDate, '%Y-%m') " +
                                "ORDER BY FUNCTION('DATE_FORMAT', i.dueDate, '%Y-%m') ASC")
                .getResultList();

        return results.stream()
                .map(obj -> new MonthlyRevenueDTO((String) obj[0], (BigDecimal) obj[1]))
                .toList();
    }
    public List<Object[]> getInventoryStatusBreakdown() {
        return entityManager.createQuery(
                        "SELECT u.status, COUNT(u) FROM Unit u GROUP BY u.status")
                .getResultList();
    }

}