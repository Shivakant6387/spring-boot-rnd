package org.example.springbootrndproject.service;


import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import org.example.springbootrndproject.dto.PdfRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfServiceImpl implements PdfService {
    @Override
    public byte[] generatePdf(PdfRequest request) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Create a PDF document
            try (Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {
                // Add content dynamically
                document.add(new Paragraph("Title: " + request.getTitle()));
                document.add(new Paragraph("Content: " + request.getContent()));
                document.add(new Paragraph("Author: " + request.getAuthor()));
            }

            // Return the PDF as byte array
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generatePdfBackGroundImage(PdfRequest request) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Create PDF document
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add background image
            String backgroundImagePath = "src/main/resources/6491428.jpg";

            ImageData imageData = ImageDataFactory.create(backgroundImagePath);
            Image backgroundImage = new Image(imageData);
            backgroundImage.setFixedPosition(0, 0);
            backgroundImage.setWidth(pdfDocument.getDefaultPageSize().getWidth());
            backgroundImage.setHeight(pdfDocument.getDefaultPageSize().getHeight());

            // Get the first page and add the background image
            PdfPage page = pdfDocument.addNewPage();
            Canvas canvas = new Canvas(page, pdfDocument.getDefaultPageSize());
            canvas.add(backgroundImage);
            canvas.close();

            // Add content over the background
            document.add(new Paragraph("Title: " + request.getTitle()).setFontSize(20).setBold());
            document.add(new Paragraph("Content: " + request.getContent()));
            document.add(new Paragraph("Author: " + request.getAuthor()));

            // Close document
            document.close();

            // Return the PDF as a byte array
            return out.toByteArray();
        }
    }
}
