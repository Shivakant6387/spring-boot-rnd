package org.example.springbootrndproject.service.pdfread.life;

import org.example.springbootrndproject.dto.ResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface LifeService {
 ResponseDto futureGeneraliIndiaLifeInsuranceCompanyLtd(MultipartFile file);
 ResponseDto  pnbMetLife(MultipartFile file);
}
