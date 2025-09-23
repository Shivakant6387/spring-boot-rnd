package org.example.springbootrndproject.controller;

import org.example.springbootrndproject.dto.PdfRequest;
import org.example.springbootrndproject.dto.PdfResponseDto;
import org.example.springbootrndproject.dto.ResponseDto;
import org.example.springbootrndproject.entity.Proposal;
import org.example.springbootrndproject.service.PdfService;
import org.example.springbootrndproject.service.pdfread.health.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    @Autowired
    private PdfService pdfService;
    @Autowired
    private HealthService readLifeAndHealth;

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

    @PostMapping("/extract")
    public ResponseEntity<PdfResponseDto> extractDetailsFromPdf(@RequestParam("file") MultipartFile file) {
        PdfResponseDto responseDto = pdfService.extractDetails(file);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/extract")
    public ResponseEntity<Proposal> extractDetailsFromPdfRead(@RequestParam("file") MultipartFile file, @RequestParam String companyName) throws Exception {
        Proposal responseDto = pdfService.proposalResponse(file,companyName);
        return ResponseEntity.ok(responseDto);
    }
}
