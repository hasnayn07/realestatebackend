package com.realestatebackend.dashboard.service;

import com.realestatebackend.dashboard.dto.DashboardKpiResponse;
import com.realestatebackend.dashboard.dto.MonthlyRevenueDTO;
import java.util.List;

public interface DashboardService {
    DashboardKpiResponse getTopLevelKpis();
    List<MonthlyRevenueDTO> getMonthlyRevenueTrend();
    List<Object[]> getInventoryStatusBreakdown();
}