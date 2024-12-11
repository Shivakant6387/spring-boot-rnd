package org.example.springbootrndproject.service;

import org.example.springbootrndproject.dto.PdfRequest;
import org.example.springbootrndproject.dto.PdfResponseDto;
import org.example.springbootrndproject.entity.Proposal;
import org.springframework.web.multipart.MultipartFile;

public interface PdfService {
    byte[] generatePdf(PdfRequest request) throws Exception;
    public byte[] generatePdfBackGroundImage(PdfRequest request) throws Exception;
    PdfResponseDto extractDetails(MultipartFile file);
    Proposal proposalResponse(MultipartFile file, String companyName) throws Exception;
}
