package com.realestatebackend.reporting.controller;

import com.realestatebackend.dashboard.service.DashboardService; // 1. IMPORT THIS
import com.realestatebackend.reporting.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ReportController {

    private final PdfExportService pdfExportService;
    private final DashboardService dashboardService; // 2. ADD THIS FIELD

    @GetMapping("/analytics/pdf")
    public ResponseEntity<byte[]> downloadAnalyticsPdf() {
        // 3. This will now work because dashboardService is defined above
        var kpis = dashboardService.getTopLevelKpis();
        byte[] pdfBytes = pdfExportService.generateAnalyticsReport(kpis);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "ShanaynLabs_Analytics.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}