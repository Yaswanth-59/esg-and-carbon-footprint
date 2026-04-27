package com.example.carboncalculator.controller;

import com.example.carboncalculator.model.*;
import com.example.carboncalculator.repository.*;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

@RestController
public class ReportController {

    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private GovernanceRepository governanceRepository;

    @GetMapping("/download-report")
    public void downloadReport(HttpServletResponse response,
                               HttpSession session) throws IOException {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            response.sendRedirect("/login");
            return;
        }

        // ===== DATA =====
        List<Calculation> envList =
                calculationRepository.findByEmailOrderByCalculationDateDesc(email);

        int environmentalScore = envList.isEmpty()
                ? 0 : (int) envList.get(0).getEcoScore();

        List<SocialData> socialList = socialRepository.findByEmail(email);
        int socialScore = socialList.isEmpty()
                ? 0 : socialList.get(socialList.size() - 1).getScore();

        List<GovernanceData> govList = governanceRepository.findByEmail(email);
        int governanceScore = govList.isEmpty()
                ? 0 : govList.get(govList.size() - 1).getScore();

        int esgScore = (environmentalScore + socialScore + governanceScore) / 3;

        // ESG Rating
        String rating;
        if (esgScore >= 80) rating = "  Excellent";
        else if (esgScore >= 65) rating = " Strong";
        else if (esgScore >= 50) rating = " Moderate";
        else rating = "High Risk";

        // ===== PDF SETUP =====
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=ESG_Report.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        com.itextpdf.kernel.pdf.PdfDocument pdf =
                new com.itextpdf.kernel.pdf.PdfDocument(writer);
        Document document = new Document(pdf);

  // ================= FORTUNE-STYLE CORPORATE COVER =================

// ===== Watermark background =====
try {
    ClassPathResource watermarkRes =
            new ClassPathResource("static/watermark.png"); // optional watermark
    byte[] wmBytes = watermarkRes.getInputStream().readAllBytes();
    ImageData wmData = ImageDataFactory.create(wmBytes);

    Image watermark = new Image(wmData)
            .scaleToFit(400, 400)
            .setOpacity(0.08f)
            .setFixedPosition(100, 250);

    document.add(watermark);
} catch (Exception ignored) {}


// ===== Company logo =====
try {
    ClassPathResource logoResource =
            new ClassPathResource("static/logo.png");
    byte[] logoBytes = logoResource.getInputStream().readAllBytes();
    ImageData logoData = ImageDataFactory.create(logoBytes);

    Image logo = new Image(logoData)
            .scaleToFit(110, 110)
            .setHorizontalAlignment(
                    com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

    document.add(logo);
} catch (Exception ignored) {}


// ===== Main corporate title =====
document.add(new Paragraph("SUSTAINABILITY REPORT 2026")
        .setBold()
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(24)
        .setFontColor(new DeviceRgb(0, 102, 153)));

document.add(new Paragraph("Environmental • Social • Governance")
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(14)
        .setFontColor(new DeviceRgb(0, 150, 136)));

document.add(new Paragraph(" "));


// ===== University section =====
document.add(new Paragraph("Andhra University College of Engineering (A)")
        .setBold()
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(14));

document.add(new Paragraph("Department of Information Technology and Computer Applications")
        .setTextAlignment(TextAlignment.CENTER));

document.add(new Paragraph("Master of Computer Applications")
        .setTextAlignment(TextAlignment.CENTER));

document.add(new Paragraph("Visakhapatnam – 530003")
        .setTextAlignment(TextAlignment.CENTER));

document.add(new Paragraph(" "));


// ===== Project title =====
document.add(new Paragraph("PROJECT REPORT")
        .setBold()
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(15)
        .setFontColor(new DeviceRgb(0, 121, 107)));

document.add(new Paragraph("Environmental, Social and Governance")
        .setBold()
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(18));

document.add(new Paragraph("Sustainability Assessment System")
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(14));

document.add(new Paragraph(" "));


// ===== Corporate-style info card =====
Table infoCard = new Table(UnitValue.createPercentArray(2))
        .useAllAvailableWidth();

Cell studentCell = new Cell()
        .add(new Paragraph("SUBMITTED BY").setBold())
        .add(new Paragraph("KUNDRAPU YASWANTH KUMAR"))
        .add(new Paragraph("Regd. No: 324207360082"))
        .setBackgroundColor(new DeviceRgb(240, 248, 255))
        .setPadding(15);

Cell guideCell = new Cell()
        .add(new Paragraph("GUIDED BY").setBold())
        .add(new Paragraph("Prof. G. Suvarna Kumar"))
        .add(new Paragraph("Assistant Professor"))
        .add(new Paragraph("Department of CSE"))
        .setBackgroundColor(new DeviceRgb(240, 248, 255))
        .setPadding(15);

infoCard.addCell(studentCell);
infoCard.addCell(guideCell);

document.add(infoCard);

document.add(new Paragraph(" "));


// ===== Academic year badge =====
Paragraph yearBadge = new Paragraph("Academic Year 2024 – 2026")
        .setTextAlignment(TextAlignment.CENTER)
        .setBold()
        .setFontColor(ColorConstants.WHITE)
        .setBackgroundColor(new DeviceRgb(0, 121, 107))
        .setPadding(8);

document.add(yearBadge);

document.add(new Paragraph("Generated ESG Performance Report")
        .setTextAlignment(TextAlignment.CENTER)
        .setFontSize(11)
        .setFontColor(new DeviceRgb(100, 100, 100)));

document.add(new AreaBreak());


        // ================= EXECUTIVE SUMMARY =================
        document.add(new Paragraph("Executive Summary")
                .setBold().setFontSize(18));

        Paragraph badge = new Paragraph("ESG Rating: " + rating)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setBackgroundColor(new DeviceRgb(0, 150, 136))
                .setFontColor(ColorConstants.WHITE)
                .setPadding(10);

        document.add(badge);
        document.add(new Paragraph(" "));

        Table summaryTable = new Table(UnitValue.createPercentArray(2))
                .useAllAvailableWidth();

        addHeaderCell(summaryTable, "Category");
        addHeaderCell(summaryTable, "Score");

        addDataCell(summaryTable, "Environmental");
        addDataCell(summaryTable, environmentalScore + "%");

        addDataCell(summaryTable, "Social");
        addDataCell(summaryTable, socialScore + "%");

        addDataCell(summaryTable, "Governance");
        addDataCell(summaryTable, governanceScore + "%");

        addDataCell(summaryTable, "Final ESG Score");
        addDataCell(summaryTable, esgScore + "%");

        document.add(summaryTable);
        document.add(new Paragraph(" "));

        // PIE CHART
        document.add(createPieChart(
                "ESG Distribution",
                new String[]{"Environmental", "Social", "Governance"},
                new int[]{environmentalScore, socialScore, governanceScore}
        ));

        document.add(new AreaBreak());

        // ================= TREND ANALYSIS =================
        document.add(new Paragraph("Environmental Trend Analysis")
                .setBold().setFontSize(16));

        document.add(createLineChart(envList));

        document.add(new AreaBreak());

        // ================= ENVIRONMENTAL HISTORY =================
        document.add(new Paragraph("Environmental History")
                .setBold().setFontSize(16));

        Table envTable = new Table(UnitValue.createPercentArray(5))
                .useAllAvailableWidth();

        addHeaderCell(envTable, "Electricity");
        addHeaderCell(envTable, "Transport");
        addHeaderCell(envTable, "Food");
        addHeaderCell(envTable, "Waste");
        addHeaderCell(envTable, "Score");

        for (Calculation c : envList) {
            addDataCell(envTable, String.valueOf(c.getElectricity()));
            addDataCell(envTable, String.valueOf(c.getTransport()));
            addDataCell(envTable, String.valueOf(c.getFood()));
            addDataCell(envTable, String.valueOf(c.getWaste()));
            addDataCell(envTable, String.valueOf(c.getEcoScore()));
        }

        document.add(envTable);
        document.add(new Paragraph(" "));

        // ================= SOCIAL HISTORY =================
        document.add(new Paragraph("Social History")
                .setBold().setFontSize(16));

        Table socialTable = new Table(UnitValue.createPercentArray(5))
                .useAllAvailableWidth();

        addHeaderCell(socialTable, "Employees");
        addHeaderCell(socialTable, "Safety");
        addHeaderCell(socialTable, "Training");
        addHeaderCell(socialTable, "Community");
        addHeaderCell(socialTable, "Score");

        for (SocialData s : socialList) {
            addDataCell(socialTable, String.valueOf(s.getEmployees()));
            addDataCell(socialTable, String.valueOf(s.getSafety()));
            addDataCell(socialTable, String.valueOf(s.getTraining()));
            addDataCell(socialTable, String.valueOf(s.getCommunity()));
            addDataCell(socialTable, String.valueOf(s.getScore()));
        }

        document.add(socialTable);
        document.add(new Paragraph(" "));

        // ================= GOVERNANCE HISTORY =================
        document.add(new Paragraph("Governance History")
                .setBold().setFontSize(16));

        Table govTable = new Table(UnitValue.createPercentArray(5))
                .useAllAvailableWidth();

        addHeaderCell(govTable, "Compliance");
        addHeaderCell(govTable, "Audit");
        addHeaderCell(govTable, "Policy");
        addHeaderCell(govTable, "Diversity");
        addHeaderCell(govTable, "Score");

        for (GovernanceData g : govList) {
            addDataCell(govTable, String.valueOf(g.getCompliance()));
            addDataCell(govTable, String.valueOf(g.getAudit()));
            addDataCell(govTable, String.valueOf(g.getPolicy()));
            addDataCell(govTable, String.valueOf(g.getDiversity()));
            addDataCell(govTable, String.valueOf(g.getScore()));
        }

        document.add(govTable);
        document.add(new Paragraph(" "));

        // ================= RECOMMENDATIONS =================
        document.add(new Paragraph("Recommended ESG Actions")
                .setBold().setFontSize(16));

        if (environmentalScore < 60) {
            document.add(new Paragraph("• Reduce electricity consumption"));
            document.add(new Paragraph("• Use shared or public transport"));
            document.add(new Paragraph("• Reduce plastic and food waste"));
        }

        if (socialScore < 60) {
            document.add(new Paragraph("• Improve employee training"));
            document.add(new Paragraph("• Strengthen safety policies"));
        }

        if (governanceScore < 60) {
            document.add(new Paragraph("• Improve compliance policies"));
            document.add(new Paragraph("• Increase audit transparency"));
        }

        if (esgScore >= 80) {
            document.add(new Paragraph("• Maintain current sustainability practices"));
        }

        document.close();
    }

    // ===== HELPER METHODS =====
    private void addHeaderCell(Table table, String text) {
        table.addCell(new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(new DeviceRgb(0, 121, 107))
                .setFontColor(ColorConstants.WHITE));
    }

    private void addDataCell(Table table, String text) {
        table.addCell(new Cell().add(new Paragraph(text)));
    }

    private Image createPieChart(String title, String[] labels, int[] values)
            throws IOException {

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < labels.length; i++) {
            dataset.setValue(labels[i], values[i]);
        }

        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        BufferedImage chartImage = chart.createBufferedImage(450, 300);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(chartImage, "png", baos);

        ImageData chartData = ImageDataFactory.create(baos.toByteArray());
        return new Image(chartData);
    }

    private Image createLineChart(List<Calculation> envList)
            throws IOException {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int i = 1;

        for (Calculation c : envList) {
            dataset.addValue(c.getEcoScore(), "Eco Score", "Entry " + i++);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Environmental Trend", "Entries", "Score", dataset);

        BufferedImage chartImage = chart.createBufferedImage(500, 300);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(chartImage, "png", baos);

        ImageData chartData = ImageDataFactory.create(baos.toByteArray());
        return new Image(chartData);
    }
}
