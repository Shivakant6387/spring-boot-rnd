package org.example.springbootrndproject.service.pdfread.health;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.springbootrndproject.dto.ResponseDto;
import org.example.springbootrndproject.exception.PdfProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HealthServiceImpl implements HealthService {
    @Override
    public ResponseDto nivaBupaHealth(MultipartFile file) throws Exception {
        ResponseDto responseDto = new ResponseDto();
        File tempFile = File.createTempFile("temp-", ".pdf");
        file.transferTo(tempFile);
        try (PDDocument document = PDDocument.load(tempFile)) {
            if (document.isEncrypted()) {
                responseDto.setMessage("PDF is encrypted, unable to process.");
                responseDto.setHttpStatus(403);
                return responseDto;
            }
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            Pattern policyPattern = Pattern.compile("Policy Number[:\\s]+(\\d+)");
            Pattern namePattern = Pattern.compile("Policyholder Name[:\\s]+([A-Z .]+)", Pattern.CASE_INSENSITIVE);
            Matcher policyMatcher = policyPattern.matcher(text);
            Matcher nameMatcher = namePattern.matcher(text);
            if (policyMatcher.find()) {
                responseDto.setPolicyNumber(policyMatcher.group(1));
            }
            if (nameMatcher.find()) {
                responseDto.setPolicyHolderName(nameMatcher.group(1).trim());
            }
            responseDto.setNetPremium(findNetPremium(text));
            responseDto.setTotalAmount(findGrossPremium(text));
            responseDto.setPolicyExpiryDate(findPolicyExpiryDate(text));
            responseDto.setInsurerName(findNivaBupaInsurer(text));
            responseDto.setIssueDate(findPolicyCommencementDate(text));
            responseDto.setMobileNumber(findMobileNumber(text));
            responseDto.setPolicyHolderAddress(findPolicyHolderAddress(text));
            responseDto.setPolicyTerm(findPolicyTerm(text));
            responseDto.setProductName(findProductName(text));
            responseDto.setIntermediaryName(findIntermediaryName(text));
            responseDto.setIntermediaryCode(findIntermediaryCode(text));
            responseDto.setIntermediaryContactNo(findIntermediaryContactNo(text));
            responseDto.setMessage("Fetched data successfully!");
            responseDto.setHttpStatus(200);
        } catch (IOException e) {
            e.printStackTrace();
            responseDto.setMessage("Error reading PDF: " + e.getMessage());
            responseDto.setHttpStatus(500);
        } finally {
            if (tempFile.exists()) tempFile.delete();
        }
        return responseDto;
    }

    @Override
    public ResponseDto careHealthInsurance(MultipartFile file) {
        ResponseDto response = new ResponseDto();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            text = text.replaceAll("\\r\\n?", "\n");
            text = text.replaceAll("\\s{2,}", " ");
            String policyNumber = null;
            Matcher policyMatcher = Pattern.compile("Policy No\\.?\\s*:?\\s*(\\d+)", Pattern.CASE_INSENSITIVE)
                    .matcher(text);
            if (policyMatcher.find()) {
                policyNumber = policyMatcher.group(1).trim();
            }
            String mobileNumber = null;
            Matcher mobileMatcher = Pattern.compile("Mobile No:?\\s*([X0-9]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (mobileMatcher.find()) {
                mobileNumber = mobileMatcher.group(1).trim();
            }

            String productName = null;
            Matcher productMatcher = Pattern.compile("Plan Name\\s+([A-Za-z ]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (productMatcher.find()) {
                productName = productMatcher.group(1).trim();
            }

            String policyExpiryDate = null;
            Matcher expiryMatcher = Pattern.compile("Policy Period - End Date\\s+Midnight\\s+([0-9A-Za-z-]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (expiryMatcher.find()) {
                policyExpiryDate = expiryMatcher.group(1).trim();
            }
            String policyStartDate = null;
            Matcher startMatcher = Pattern.compile(
                    "Policy Period - Start Date\\s+.*?([0-9]{2}-[A-Za-z]{3}-[0-9]{4})",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            if (startMatcher.find()) {
                policyStartDate = startMatcher.group(1).trim();
            }
            // Extract Plan Name
            String planName = null;
            Matcher planMatcher = Pattern.compile(
                    "Plan Name\\s+([A-Za-z ]+)",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            if (planMatcher.find()) {
                planName = planMatcher.group(1).trim();
            }
            // Extract Nominee Details
            String nomineeName = null;
            String nomineeRelationship = null;

            Matcher nomineeMatcher = Pattern.compile(
                    "Nominee Details[\\s\\S]*?\\n\\s*1\\s+([A-Za-z ]+)\\s+([A-Za-z]+)",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            if (nomineeMatcher.find()) {
                nomineeName = nomineeMatcher.group(1).trim();
                nomineeRelationship = nomineeMatcher.group(2).trim();
            }
            // Extract Intermediary Details
            String intermediaryName = null;
            String intermediaryCode = null;
            String intermediaryContactNo = null;

            Matcher intermediaryMatcher = Pattern.compile(
                    "Intermediary Details[\\s\\S]*?Name Code Contact Details\\s*\\n([\\s\\S]*?)\\s+(\\d{6,})\\s+(\\d{6,})",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            if (intermediaryMatcher.find()) {
                // Capture full block before the numbers (name can span multiple lines)
                intermediaryName = intermediaryMatcher.group(1).replaceAll("\\n", " ").trim();
                intermediaryCode = intermediaryMatcher.group(2).trim();
                intermediaryContactNo = intermediaryMatcher.group(3).trim();
            }
            Matcher insuredMatcher = Pattern.compile(
                    "Details of Insured Person[\\s\\S]*?(?=Nominee Details)",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            String policyTerm = null;
            if (policyStartDate != null && policyExpiryDate != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

                LocalDate start = LocalDate.parse(policyStartDate, formatter);
                LocalDate end = LocalDate.parse(policyExpiryDate, formatter);

                long days = ChronoUnit.DAYS.between(start, end) + 1;

                if (days >= 365) {
                    policyTerm = (days / 365) + " Year";
                } else if (days >= 30) {
                    policyTerm = (days / 30) + " Months";
                } else {
                    policyTerm = days + " Days";
                }
            }
            String insurerName = null;

            Matcher insurerMatcher = Pattern.compile(
                    "Care Health Insurance Limited",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            if (insurerMatcher.find()) {
                insurerName = "Care Health Insurance Limited";
            }

            response.setInsurerName(insurerName);
            response.setPolicyTerm(policyTerm);
            response.setIntermediaryName(intermediaryName);
            response.setIntermediaryCode(intermediaryCode);
            response.setIntermediaryContactNo(intermediaryContactNo);
            response.setNomineeName(nomineeName);
            response.setNomineeRelationship(nomineeRelationship);
            response.setProductName(planName);
            response.setPolicyExpiryDate(policyExpiryDate);
            response.setIssueDate(policyStartDate);
            response.setMobileNumber(mobileNumber);
            response.setPolicyNumber(policyNumber);
            response.setMessage("Fetched data successfully!");
            response.setHttpStatus(HttpStatus.OK.value());

        } catch (IOException e) {
            throw new PdfProcessingException("Failed to read or parse Care Health Insurance PDF", e);
        }

        return response;
    }

    private String findIntermediaryName(String text) {
        String[] lines = text.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toLowerCase().contains("intermediary name")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String line = lines[j].trim();
                    if (!line.isEmpty() && !line.matches("[-]+")) { // skip lines with just hyphens
                        // Remove trailing hyphens or extra symbols
                        line = line.replaceAll("[-]+$", "").trim();
                        return line;
                    }
                }
            }
        }
        return null;
    }

    private String findIntermediaryCode(String text) {
        String[] lines = text.split("\\r?\\n");
        Pattern codePattern = Pattern.compile("[A-Za-z0-9]+");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toLowerCase().contains("intermediary code")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String line = lines[j].trim();
                    if (!line.isEmpty()) {
                        Matcher matcher = codePattern.matcher(line);
                        if (matcher.matches()) {
                            return line;
                        }
                    }
                }
            }
        }
        return null;
    }

    private String findIntermediaryContactNo(String text) {
        String[] lines = text.split("\\r?\\n");
        Pattern contactPattern = Pattern.compile("\\d{10,14}");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].toLowerCase().contains("intermediary contact")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String line = lines[j].trim();
                    if (!line.isEmpty()) {
                        Matcher matcher = contactPattern.matcher(line.replaceAll("[^\\d]", ""));
                        if (matcher.matches()) {
                            return line.replaceAll("[^\\d]", "");
                        }
                    }
                }
            }
        }
        return null;
    }


    private String findGrossPremium(String text) {

        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Gross Premium \\(Rs\\.\\)[:\\s]*([\\d,]+(?:\\.\\d{1,2})?)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findNetPremium(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Net Premium / Taxable value \\(Rs\\.\\)[:\\s]*([\\d,]+(?:\\.\\d{1,2})?)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }


    private String findPolicyExpiryDate(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Policy Expiry Date\\s*[:#]?\\s*(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}-\\w{3}-\\d{4}|\\d{4}-\\d{2}-\\d{2})",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findMobileNumber(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Mobile No\\.[:\\s]*([+]?\\d{10,14})",  // matches 10-14 digits with optional +
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);

        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findProductName(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Product Name[:\\s]+([A-Za-z0-9 .&\\-]+)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findPolicyHolderAddress(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Policyholder Address[:\\s]+(.+?)(?=Policy Number|Mobile|$)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findPolicyTerm(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "(?:Policy Term|Term)[:\\s]+([\\d]+\\s*(?:years|Yrs)?)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findNivaBupaInsurer(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "(Niva Bupa Health Insurance)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private String findPolicyCommencementDate(String text) {
        String normalizedText = text.replaceAll("\\r?\\n", " ").replaceAll("\\s+", " ").trim();
        Pattern pattern = Pattern.compile(
                "Policy Commencement Date\\s*[:#]?\\s*(\\d{1,2}/\\d{1,2}/\\d{4}|\\d{1,2}-\\w{3}-\\d{4}|\\d{4}-\\d{2}-\\d{2})",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(normalizedText);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
}
