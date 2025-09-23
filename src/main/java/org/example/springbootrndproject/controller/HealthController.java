package org.example.springbootrndproject.controller;

import org.example.springbootrndproject.dto.ResponseDto;
import org.example.springbootrndproject.service.pdfread.health.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/health")
public class HealthController {
    @Autowired
    private HealthService healthService;

    @PostMapping("/niva-bupa-health")
    public ResponseEntity<ResponseDto> nivaBupaHealth(@RequestParam("file") MultipartFile file) {
        try {
            ResponseDto responseDto = healthService.nivaBupaHealth(file);
            HttpStatus status = HttpStatus.resolve(responseDto.getHttpStatus());
            if (status == null) {
                status = HttpStatus.OK;
            }
            return new ResponseEntity<>(responseDto, status);
        } catch (Exception e) {
            ResponseDto errorDto = new ResponseDto();
            errorDto.setMessage("Server error: " + e.getMessage());
            errorDto.setHttpStatus(500);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/care-health")
    public ResponseEntity<ResponseDto> uploadCareHealth(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            ResponseDto errorResponse = new ResponseDto();
            errorResponse.setMessage("No file uploaded");
            errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(errorResponse);
        }

        ResponseDto response = healthService.careHealthInsurance(file);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
