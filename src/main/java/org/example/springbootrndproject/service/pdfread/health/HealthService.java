package org.example.springbootrndproject.service.pdfread.health;

import org.example.springbootrndproject.dto.ResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface HealthService {
    ResponseDto nivaBupaHealth(MultipartFile file) throws Exception;
    public ResponseDto careHealthInsurance(MultipartFile file) throws Exception;
}
