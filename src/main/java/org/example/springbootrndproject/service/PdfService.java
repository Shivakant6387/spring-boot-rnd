package org.example.springbootrndproject.service;

import org.example.springbootrndproject.dto.PdfRequest;

public interface PdfService {
    byte[] generatePdf(PdfRequest request) throws Exception;
    public byte[] generatePdfBackGroundImage(PdfRequest request) throws Exception;
}
