package org.example.springbootrndproject.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class PdfRequest {
    private String title;
    private String content;
    private String author;
}
