package org.example.springbootrndproject.exception;

import org.example.springbootrndproject.dto.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PdfProcessingException.class)
    public ResponseEntity<ResponseDto> handlePdfProcessingException(PdfProcessingException ex) {
        ResponseDto response = new ResponseDto();
        response.setMessage(ex.getMessage());
        response.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> handleGenericException(Exception ex) {
        ResponseDto response = new ResponseDto();
        response.setMessage("Unexpected server error: " + ex.getClass().getSimpleName());
        response.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
