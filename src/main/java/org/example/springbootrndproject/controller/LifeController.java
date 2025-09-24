package org.example.springbootrndproject.controller;

import org.example.springbootrndproject.dto.ResponseDto;
import org.example.springbootrndproject.service.pdfread.life.LifeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/life")
public class LifeController {

    private final LifeService lifeService;

    @Autowired
    public LifeController(LifeService lifeService) {
        this.lifeService = lifeService;
    }

    @PostMapping("/future-generali")
    public ResponseEntity<ResponseDto> uploadPolicy(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            ResponseDto errorResponse = new ResponseDto();
            errorResponse.setMessage("No file uploaded");
            errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(errorResponse);
        }

        ResponseDto response = lifeService.futureGeneraliIndiaLifeInsuranceCompanyLtd(file);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @PostMapping("/pnb-metlife")
    public ResponseEntity<ResponseDto> uploadPnbMetLife(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            ResponseDto errorResponse = new ResponseDto();
            errorResponse.setMessage("No file uploaded");
            errorResponse.setHttpStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(errorResponse);
        }

        ResponseDto response = lifeService.pnbMetLife(file);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
    @PostMapping("/pramerica-life")
    public ResponseEntity<ResponseDto> uploadPramericaPolicy(@RequestParam("file") MultipartFile file) {
        ResponseDto response = lifeService.pramericaLifeRockSolidFuture(file);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
