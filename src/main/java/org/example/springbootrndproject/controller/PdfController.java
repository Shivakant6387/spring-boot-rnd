package org.example.springbootrndproject.controller;

import org.example.springbootrndproject.dto.PdfRequest;
import org.example.springbootrndproject.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    @Autowired
    private PdfService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody PdfRequest request) {
        try {
            // Generate the PDF using the service
            byte[] pdfBytes = pdfService.generatePdf(request);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "generated.pdf");

            // Return the PDF as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }
    @PostMapping("/generate/backGroundImage")
    public ResponseEntity<byte[]> generatePdfBackGroundImage(@RequestBody PdfRequest request) {
        try {
            // Generate the PDF using the service
            byte[] pdfBytes = pdfService.generatePdfBackGroundImage(request);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "generated.pdf");

            // Return the PDF as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

}
