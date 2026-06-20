package com.realestatebackend.reporting.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.realestatebackend.dashboard.dto.DashboardKpiResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;

@Service
public class PdfExportService {

    public byte[] generateAnalyticsReport(DashboardKpiResponse kpis) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new WatermarkPageEvent()); // Attaches the watermark

            document.open();

            // Report Content
            document.add(new Paragraph("Shanayn Labs: Agency Analytics Report", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20)));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Total Inventory: " + kpis.totalUnits()));
            document.add(new Paragraph("Available Units: " + kpis.availableUnits()));
            document.add(new Paragraph("Expected Revenue: Rs. " + kpis.totalExpectedRevenue()));
            document.add(new Paragraph("Cash Collected: Rs. " + kpis.totalCashCollected()));
            document.add(new Paragraph("Active Defaulters: " + kpis.activeDefaulters()));

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
        return out.toByteArray();
    }

    private static class WatermarkPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            try {
                PdfContentByte canvas = writer.getDirectContentUnder();

                // Set Watermark Text
                Phrase watermark = new Phrase("Powered by ShanaynLabs",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 60, java.awt.Color.LIGHT_GRAY));

                // Rotate and center the watermark text
                ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, watermark,
                        300, 400, 45); // 45 degree angle
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}