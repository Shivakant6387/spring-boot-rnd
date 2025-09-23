package org.example.springbootrndproject.service;


import com.google.gson.Gson;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Image;
import org.example.springbootrndproject.dto.PdfRequest;
import org.example.springbootrndproject.dto.PdfResponseDto;
import org.example.springbootrndproject.dto.ResponseDto;
import org.example.springbootrndproject.entity.Proposal;
import org.example.springbootrndproject.repository.ProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfServiceImpl implements PdfService {
    @Autowired
    private ProposalRepository proposalRepository;

    @Override
    public byte[] generatePdf(PdfRequest request) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Create a PDF document
            try (Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {
                // Add content dynamically
                document.add(new Paragraph("Title: " + request.getTitle()));
                document.add(new Paragraph("Content: " + request.getContent()));
                document.add(new Paragraph("Author: " + request.getAuthor()));
            }

            // Return the PDF as byte array
            return out.toByteArray();
        }
    }

    @Override
    public byte[] generatePdfBackGroundImage(PdfRequest request) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Initialize PDF writer
            PdfWriter writer = new PdfWriter(out);

            // Create PDF document
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add background image
            String backgroundImagePath = "src/main/resources/6491428.jpg";

            ImageData imageData = ImageDataFactory.create(backgroundImagePath);
            Image backgroundImage = new Image(imageData);
            backgroundImage.setFixedPosition(0, 0);
            backgroundImage.setWidth(pdfDocument.getDefaultPageSize().getWidth());
            backgroundImage.setHeight(pdfDocument.getDefaultPageSize().getHeight());

            // Get the first page and add the background image
            PdfPage page = pdfDocument.addNewPage();
            Canvas canvas = new Canvas(page, pdfDocument.getDefaultPageSize());
            canvas.add(backgroundImage);
            canvas.close();

            // Add content over the background
            document.add(new Paragraph("Title: " + request.getTitle()).setFontSize(20).setBold());
            document.add(new Paragraph("Content: " + request.getContent()));
            document.add(new Paragraph("Author: " + request.getAuthor()));

            // Close document
            document.close();

            // Return the PDF as a byte array
            return out.toByteArray();
        }
    }

    @Override
    public PdfResponseDto extractDetails(MultipartFile file) {
        PdfResponseDto responseDto = new PdfResponseDto();
        try {
            // Convert MultipartFile to a temporary file
            File tempFile = File.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);

            // Extract text from the PDF
            try (PDDocument document = PDDocument.load(tempFile)) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                Gson gson = new Gson();
                String relianceCoverage = gson.toJson(text);
                System.out.println(relianceCoverage);
                // Process extracted text (implement your logic here)
                responseDto.setText(text);
                responseDto.setMessage("PDF processed successfully");
            } finally {
                tempFile.delete(); // Cleanup
            }
        } catch (IOException e) {
            responseDto.setMessage("Error processing PDF: " + e.getMessage());
        }
        return responseDto;
    }

    @Override
    public Proposal proposalResponse(MultipartFile file, String companyName) {
        Proposal proposalResponse = new Proposal();
        if (file == null || file.isEmpty()) {
            proposalResponse.setMessage("No file provided");
            proposalResponse.setHttpStatus(400);
            return proposalResponse;
        }
        try {
            File tempFile = File.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);
            try (PDDocument document = PDDocument.load(tempFile)) {
                if (document.isEncrypted()) {
                    proposalResponse.setMessage("PDF is encrypted, unable to process.");
                    proposalResponse.setHttpStatus(403);
                    return proposalResponse;
                }

                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);

                if (text == null || text.trim().isEmpty()) {
                    proposalResponse.setMessage("PDF is empty or could not extract text.");
                    proposalResponse.setHttpStatus(400);
                    return proposalResponse;
                }

                Map<String, String> extractedDetails = new LinkedHashMap<>();
                Map<String, String> reliance = new LinkedHashMap<>();
                Map<String, String> extractAddressDetails = new LinkedHashMap<>();
                Map<String, String> extractDetailsReliancePrevious = new LinkedHashMap<>();
                Map<String, String> extractRegistrationNumber = new LinkedHashMap<>();
                Map<String, String> extractPeriodOfInsurance = new LinkedHashMap<>();
                Map<String, String> extractPremiumDetails = new LinkedHashMap<>();
                Map<String, String> extractPolicyDetails = new LinkedHashMap<>();
                Map<String, String> extractPolicyDetailsMobile = new LinkedHashMap<>();
                if (companyName.equals("hdfc")) {
                    extractedDetails = extractDetails(text);
                    extractPremiumDetails = extractPremiumDetails(text);
                    extractPolicyDetails = extractPolicyDetails(text);
                    extractAddressDetails = extractAddressDetails(text);
                    proposalResponse.setCompanyName(companyName);
                    proposalResponse.setMake(extractedDetails.getOrDefault("Make", "Not Found"));
                    proposalResponse.setModel(extractedDetails.getOrDefault("Model", "Not Found"));
                    proposalResponse.setVehicleRegistertionNo(extractedDetails.getOrDefault("Registration No", "Not Found"));
                    proposalResponse.setRtoName(extractedDetails.getOrDefault("RTO", "Not Found"));
                    proposalResponse.setChassisNumber(extractedDetails.getOrDefault("Chassis No", "Not Found"));
                    proposalResponse.setEngineNumber(extractedDetails.getOrDefault("Engine No", "Not Found"));
                    proposalResponse.setVehicleCc(extractedDetails.getOrDefault("Cubic Capacity", "Not Found"));
                    proposalResponse.setSeatingCapacity(extractedDetails.getOrDefault("Seats", "Not Found"));
                    proposalResponse.setManufactureYear(extractedDetails.getOrDefault("Year of Manufacture", "Not Found"));
                    proposalResponse.setPolicyNumber(extractedDetails.getOrDefault("Policy No", "Not Found"));
                    proposalResponse.setPreviousYearPolicyExpiryDate(extractedDetails.getOrDefault("Issuance Date", "Not Found"));
                    proposalResponse.setPreviousPolicyNumber(extractedDetails.getOrDefault("Previous Policy No", "Not Found"));
                    proposalResponse.setPreviousNCB(extractedDetails.getOrDefault("NCB", "Not Found"));
                    proposalResponse.setBasicOD(extractPremiumDetails.getOrDefault("Basic Own Damage Premium", "Not Found"));
                    proposalResponse.setBasicTP(extractPremiumDetails.getOrDefault("Basic Third Party Liability", "Not Found"));
                    proposalResponse.setTotalPremium(extractPremiumDetails.getOrDefault("Total Premium", "Not Found"));
                    proposalResponse.setNetPremium(extractPremiumDetails.getOrDefault("Total Package Premium", "Not Found"));
                    proposalResponse.setNewPolicyStartDate(extractPolicyDetails.getOrDefault("Policy Period Start Date", "Not Found"));
                    proposalResponse.setNewPolicyExpireDate(extractPolicyDetails.getOrDefault("Policy Period End Date", "Not Found"));
                    proposalResponse.setAddresses(extractAddressDetails.getOrDefault("C/O", "Not Found"));
                    proposalResponse.setFullNameAsRC(extractAddressDetails.getOrDefault("Full Name", "Not Found"));
                    proposalResponse.setMobileNo(extractAddressDetails.getOrDefault("Tel.", "Not Found"));
                    proposalResponse.setVariant(extractedDetails.getOrDefault("Variant", "Not Found"));
                    proposalResponse.setCurrentNCB(extractPremiumDetails.getOrDefault("No Claim Bonus", "Not Found"));
                    proposalResponse.setEmailId(extractedDetails.getOrDefault("Email", "Not Found"));
                } else if (companyName.equals("reliance")) {
                    reliance = extractDetailsReliance(text);
                    extractPeriodOfInsurance = extractPeriodOfInsurance(text);
                    extractRegistrationNumber = extractRegistrationNumber(text);
                    extractPolicyDetailsMobile = extractPolicyDetailsMobile(text);
                    extractDetailsReliancePrevious = extractDetailsReliancePrevious(text);
                    proposalResponse.setPolicyNumber(reliance.getOrDefault("Policy Number", "Not Found"));
                    proposalResponse.setFullNameAsRC(reliance.getOrDefault("Insured Name", "Not Found"));
                    proposalResponse.setEmailId(reliance.getOrDefault("Email-ID", "Not Found"));
                    proposalResponse.setAddresses(reliance.getOrDefault("Communication Address", "Not Found"));
                    proposalResponse.setSeatingCapacity(reliance.getOrDefault("Seating Capacity", "Not Found"));
                    proposalResponse.setManufactureYear(reliance.getOrDefault("Mfg. Month & Year", "Not Found"));
                    proposalResponse.setRtoName(reliance.getOrDefault("RTO Location", "Not Found"));
                    proposalResponse.setMake(reliance.getOrDefault("Make", "Not Found"));
                    proposalResponse.setEngineNumber(reliance.getOrDefault("Engine Number", "Not Found"));
                    proposalResponse.setChassisNumber(reliance.getOrDefault("Chassis Number", "Not Found"));
                    proposalResponse.setMobileNo(extractPolicyDetailsMobile.getOrDefault("Phone", "Not Found"));
                    proposalResponse.setTotalPremium(reliance.getOrDefault("Final Amount", "Not Found"));
                    proposalResponse.setBasicTP(reliance.getOrDefault("Liability Premium", "Not Found"));
                    proposalResponse.setBasicOD(reliance.getOrDefault("Own Damage Premium", "Not Found"));
                    proposalResponse.setManufactureYear(reliance.getOrDefault("Mfg. Month & Year", "Not Found"));
                    proposalResponse.setVehicleRegistertionNo(extractRegistrationNumber.getOrDefault("Registration Number", "Not Found"));
                    proposalResponse.setModel(reliance.getOrDefault("Model", "Not Found"));
                    proposalResponse.setCurrentPolicyType(extractRegistrationNumber.getOrDefault("Policy Type", "Not Found"));
                    proposalResponse.setPreviousPolicyNumber(extractDetailsReliancePrevious.getOrDefault("Previous Policy Number", "Not Found"));
                    proposalResponse.setPreviousYearPolicyExpiryDate(extractDetailsReliancePrevious.getOrDefault("End Date", "Not Found"));
                    proposalResponse.setNewPolicyStartDate(extractPeriodOfInsurance.getOrDefault("Period of Insurance From", "Not Found"));
                    proposalResponse.setNewPolicyExpireDate(extractPeriodOfInsurance.getOrDefault("Period of Insurance To", "Not Found"));
                    proposalResponse.setVehicleCc(reliance.getOrDefault("Cubic Capacity", "Not Found"));
                }
                proposalResponse.setMessage("PDF processed successfully");
                proposalResponse.setHttpStatus(200);
                proposalRepository.save(proposalResponse);
            } finally {
                tempFile.delete();
            }
        } catch (IOException e) {
            proposalResponse.setMessage("Error processing PDF: " + e.getMessage());
            proposalResponse.setHttpStatus(500);
        }

        return proposalResponse;
    }


    private static Map<String, String> extractDetails(String text) {
        Map<String, String> details = new LinkedHashMap<>();

        // Define regex patterns for fields
        String[] patterns = {
                "Make\\s*([A-Za-z0-9]+)", // Make
                "Model\\s*([^\\n]+?)(?=\\s*Period of|$)",
                "Model[^\\n]+-([A-Za-z0-9.\\s]+)",
                "Policy No\\.\\s*([A-Za-z0-9 ]+)",
                "Registration No\\s*([A-Za-z0-9-]+)",
                "RTO\\s*[:]?\\s*([^\\n]+?)(?=\\s*Issuance Date|$)",
                "Chassis No\\.\\s*([^\\n]+)",
                "Invoice No\\.\\s*([A-Za-z0-9]+)",
                "Cubic Capacity\\s*/Watts\\s*(\\d+)",
                "Seats\\s*(\\d+)",
                "Year of Manufacture\\s*(\\d{4})",
                "Body Type\\s*([^\\n]+)",
                "Engine No\\.\\s*([^\\n]+)",
                "Issuance Date\\s*([\\d/]+)",
                "Customer Id\\s*(\\d+)",
                "Total Premium\\s*\\(`\\)\\s*(\\d+)",
                "Previous Policy No\\.\\s*([A-Za-z0-9]+)",
                "NCB\\s*(\\d+%)",
                "Email ID\\s*([^\\n]+)"
        };

        // Define field names corresponding to the patterns
        String[] fields = {
                "Make", "Model", "Variant", "Policy No", "Registration No", "RTO", "Chassis No",
                "Invoice No", "Cubic Capacity", "Seats", "Year of Manufacture", "Body Type",
                "Engine No", "Issuance Date", "Customer Id", "Total Premium", "Previous Policy No",
                "NCB", "Email"
        };

        // Iterate over patterns and match against text
        for (int i = 0; i < patterns.length; i++) {
            Pattern pattern = Pattern.compile(patterns[i]);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String matchedValue = matcher.group(1).trim();

                // Clean up unwanted patterns in the matched value
                matchedValue = matchedValue.replaceAll("\\r\\nInsurance\\r\\nFrom \\d{2} \\w{3}", "").trim(); // Remove specific pattern
                matchedValue = matchedValue.replaceAll("Period of.*", "").trim(); // Remove "Period of..." and beyond
                matchedValue = matchedValue.replaceAll("EIA No\\. Not provided", "").trim(); // Remove "EIA No. Not provided"
                matchedValue = matchedValue.replaceAll("Invoice No\\.\\s*\\d+", "").trim(); // Remove "Invoice No." and digits

                details.put(fields[i], matchedValue);
            } else {
                details.put(fields[i], "Not Found");
            }
        }
        return details;
    }

    private static Map<String, String> extractPremiumDetails(String text) {
        Map<String, String> details = new LinkedHashMap<>();

        // Patterns for extracting premium-related details
        String[] premiumPatterns = {
                "Basic Own Damage\\s*(\\d+)",  // Basic Own Damage
                "Basic Third Party Liability\\s*(\\d+)",  // Basic Third Party Liability
                "Total Basic Premium\\s*(\\d+)",  // Total Basic Premium
                "PA Cover for Owner Driver of\\s*(\\d+).*?(\\d+)",  // PA Cover for Owner Driver (captures both amounts)
                "No Claim Bonus \\(\\d+%\\)\\s*(\\d+)",  // No Claim Bonus
                "Net Liability Premium\\s*\\(b\\)\\s*(\\d+)",  // Net Liability Premium
                "Total - Less\\s*(\\d+)",  // Total Less
                "Total Package Premium\\s*\\(a\\+b\\)\\s*(\\d+)",  // Total Package Premium
                "Integrated Tax\\s*(\\d+)%\\s*(\\d+)",  // Integrated Tax
                "Net Own Damage Premium\\s*\\(a\\)\\s*(\\d+)",  // Net Own Damage Premium
                "Total Premium\\s*(\\d+)"  // Total Premium
        };

        // Define the fields corresponding to the above patterns
        String[] fields = {
                "Basic Own Damage Premium", "Basic Third Party Liability", "Total Basic Premium",
                "PA Cover for Owner Driver", "No Claim Bonus", "Net Liability Premium",
                "Total Less", "Total Package Premium", "Integrated Tax",
                "Net Own Damage Premium", "Total Premium"
        };

        // Loop through the patterns and extract the corresponding fields
        for (int i = 0; i < premiumPatterns.length; i++) {
            Pattern pattern = Pattern.compile(premiumPatterns[i]);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String matchedValue = matcher.group(1).trim();
                if (fields[i].equals("PA Cover for Owner Driver")) {
                    matchedValue += " Premium: " + matcher.group(2).trim();  // Concatenate the premium amount
                }
                details.put(fields[i], matchedValue);
            } else {
                // If no match is found, mark as "Not Found"
                details.put(fields[i], "Not Found");
            }
        }

        return details;
    }

    private static Map<String, String> extractPolicyDetails(String text) {
        Map<String, String> details = new LinkedHashMap<>();

        // Define regex patterns for all fields
        String policyYearPattern = "Policy Year\\s*(\\w+)";
        String policyPeriodPattern = "From\\s*([\\d/]+)\\s*To\\s*([\\d/]+)";
        String forVehiclePattern = "For the Vehicle\\s*\\(`\\)\\s*(\\d+)";
        String trailerPattern = "Trailer\\s*\\(`\\)\\s*(\\d+)";
        String nonElectricalAccPattern = "Non Electrical Acc\\.\\s*\\(`\\)\\s*(\\d+)";
        String electricalAccPattern = "Electrical Acc\\.\\s*\\(`\\)\\s*(\\d+)";
        String cngLpgKitPattern = "CNG/LPG Kit\\s*\\(`\\)\\s*(\\d+)";
        String totalIdvPattern = "Total IDV\\s*\\(`\\)\\s*([\\d,]+)";

        // Compile patterns and match against text
        details.put("Policy Year", extractValue(policyYearPattern, text));
        details.put("Policy Period Start Date", extractValue(policyPeriodPattern, text, 1));
        details.put("Policy Period End Date", extractValue(policyPeriodPattern, text, 2));
        details.put("For the Vehicle", extractValue(forVehiclePattern, text));
        details.put("Trailer", extractValue(trailerPattern, text));
        details.put("Non Electrical Accessories", extractValue(nonElectricalAccPattern, text));
        details.put("Electrical Accessories", extractValue(electricalAccPattern, text));
        details.put("CNG/LPG Kit", extractValue(cngLpgKitPattern, text));
        details.put("IDV", extractValue(totalIdvPattern, text));

        return details;
    }

    private static String extractValue(String pattern, String text) {
        return extractValue(pattern, text, 1);
    }

    private static String extractValue(String pattern, String text, int group) {
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(group).trim();
        }
        return "Not Found";
    }

    private static Map<String, String> extractAddressDetails(String text) {
        Map<String, String> details = new LinkedHashMap<>();
        String fullNamePattern = "(?m)^(MR\\s+\\w+\\s+\\w+)";
        String coPattern = "(?m)C/O:([^\\n]+)";
        String telPattern = "(?m)Tel\\.\\s*(\\S+)";

        // Extract the full name
        details.put("Full Name", extractValues(fullNamePattern, text));

        // Extract C/O details
        details.put("C/O", extractValues(coPattern, text));

        // Extract telephone number
        details.put("Tel.", extractValues(telPattern, text));

        return details;
    }

    private static String extractValues(String pattern, String text) {
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "Not Found";
    }

    public static Map<String, String> extractDetailsReliance(String text) {
        Map<String, String> details = new LinkedHashMap<>();
        Map<String, String> patterns = new LinkedHashMap<>();
        patterns.put("Engine Number", "Engine No\\. / Chassis No\\.\\s*([^/]+)\\s*/\\s*([^\\n]+)");
        patterns.put("Policy Number", "Policy Number\\s*:\\s*([A-Za-z0-9]+)");
        patterns.put("Proposal/Covernote No", "Proposal/Covernote No\\s*:\\s*([A-Za-z0-9]+)");
        patterns.put("Insured Name", "Insured Name\\s*:\\s*([^\\n]+)");
        patterns.put("Period of Insurance", "Period of Insurance\\s*:\\s*From\\s*([^\\n]+)");
        patterns.put("Communication Address", "Communication Address & Place of Supply\\s*:\\s*([^\\n]+)");
        patterns.put("Email-ID", "Email-ID\\s*:\\s*([^\\s]+)");
        patterns.put("Make", "Make / Model\\s*([^/]+)\\s*/\\s*([^/]+)\\s*/\\s*([^\\n]+)");
        patterns.put("Seating Capacity", "Seating Capacity[\\s\\S]*?Including\\s*driver[\\s\\S]*?([0-9]+)");
        patterns.put("Total Premium", "Total Premium \\(₹\\)\\s*([0-9,.]+)");
        patterns.put("Total IDV", "Total IDV \\(₹\\)\\s*([0-9,.]+)");
        patterns.put("Own Damage Premium", "TOTAL OWN DAMAGE PREMIUM\\s*([0-9,.]+)");
        patterns.put("Liability Premium", "TOTAL LIABILITY PREMIUM\\s*([0-9,.]+)");
        patterns.put("Final Amount", "TOTAL PREMIUM PAYABLE \\(₹\\)\\s*([0-9,.]+)");
        patterns.put("Nominee Detail", "PA Owner Driver Nominee Name\\s*(\\S.+?)\\s+([0-9]+)\\s+(\\S+.*?)\\s+Appointee Name");
        patterns.put("RTO Location", "RTO Location\\s*([^\\n]+)");
        patterns.put("Mfg. Month & Year", "Mfg\\. Month & Year\\s*([^\\n]+)");
        patterns.put("Model", "Type of Body/Model\\s*([^/]+)\\s*/\\s*([^\\n]+)");
        patterns.put("Cubic Capacity", "Cubic Capacity\\s*(\\d+)");
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            if (entry.getKey().equals("Engine Number")) {
                Pattern pattern = Pattern.compile(entry.getValue(), Pattern.DOTALL);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    details.put("Engine Number", matcher.group(1).trim()); // Extract Engine Number
                    details.put("Chassis Number", matcher.group(2).trim()); // Extract Chassis Number
                } else {
                    details.put("Engine Number", "Not Found");
                    details.put("Chassis Number", "Not Found");
                }
            } else if (entry.getKey().equals("Nominee Detail")) {
                Pattern pattern = Pattern.compile(entry.getValue(), Pattern.DOTALL);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    details.put("Nominee Name", matcher.group(1).trim());  // Extract Nominee Name
                    details.put("Nominee Age", matcher.group(2).trim());  // Extract Nominee Age
                    details.put("Nominee Relation", matcher.group(3).trim());  // Extract Relation with Owner Driver
                } else {
                    details.put("Nominee Name", "Not Found");
                    details.put("Nominee Age", "Not Found");
                    details.put("Nominee Relation", "Not Found");
                }
            } else if (entry.getKey().equals("Model")) {
                // Special handling for Model
                Pattern pattern = Pattern.compile(entry.getValue(), Pattern.DOTALL);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    details.put("Body Type", matcher.group(1).trim());  // Extract Body Type
                    details.put("Model", matcher.group(2).trim());  // Extract Model
                } else {
                    details.put("Body Type", "Not Found");
                    details.put("Model", "Not Found");
                }
            } else {
                Pattern pattern = Pattern.compile(entry.getValue(), Pattern.DOTALL);
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    details.put(entry.getKey(), matcher.group(1).trim());
                } else {
                    details.put(entry.getKey(), "Not Found");
                }
            }
        }
        return details;
    }

    public static Map<String, String> extractDetailsReliancePrevious(String text) {
        Map<String, String> details = new LinkedHashMap<>();
        Map<String, String> patterns = new LinkedHashMap<>();

        // Define regex patterns for other fields
        patterns.put("Date of Purchase", "Date of purchase of the Vehicle by the Proposer\\.\\s*([\\d-]+)");
        patterns.put("Vehicle Condition", "Whether the vehicle was new or second hand at the time of purchase\\?\\s*(New|Second Hand)");
        patterns.put("Private Usage Purpose", "Private, Social, Domestic, Pleasure & Professional Purpose\\?\\s*(Yes|No)");
        patterns.put("Goods Carriage Usage Purpose", "Carriage of goods other than samples or personal language\\?\\s*(Yes|No)");
        patterns.put("Is Vehicle in Good Condition", "Is the vehicle in good condition\\?\\s*(Yes|No)");
        patterns.put("Previous Insurer Name", "Name of the previous insurer\\s*(.+?)\\s*");
        patterns.put("Previous Policy Number", "Previous Policy Number\\s*([\\d]+)");
        patterns.put("Period of Insurance", "Period of Insurance From\\s*([\\d-]+)\\s*To\\s*([\\d-]+)");

        // Loop through patterns to extract matching data
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue(), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                details.put(entry.getKey(), matcher.group(1).trim());
                if (matcher.groupCount() > 1) {
                    details.put("End Date", matcher.group(2).trim());
                }
            } else {
                details.put(entry.getKey(), "Not Found");
            }
        }

        // Define regex pattern to extract mobile number
        String mobileNumberPattern = "(?:Mobile|Phone)\\s*(:?\\d{10})";  // Adjust the pattern if needed
        Pattern mobilePattern = Pattern.compile(mobileNumberPattern);
        Matcher mobileMatcher = mobilePattern.matcher(text);
        if (mobileMatcher.find()) {
            details.put("Mobile Number", mobileMatcher.group(1).trim());
        } else {
            details.put("Mobile Number", "Not Found");
        }

        // Define address pattern
        Pattern addressPattern = Pattern.compile("Address of previous insurer[^\\n]+\\s*(.+)\\s*(\\w+)(\\d+)([A-Za-z\\s]+)([A-Za-z\\s]+)([A-Za-z]+)");
        Matcher addressMatcher = addressPattern.matcher(text);
        if (addressMatcher.find()) {
            details.put("Street Address", addressMatcher.group(1).trim());
            details.put("Area", addressMatcher.group(2).trim());
            details.put("Pin Code", addressMatcher.group(3).trim());
            details.put("City", addressMatcher.group(4).trim());
            details.put("State", addressMatcher.group(5).trim());
            details.put("Country", addressMatcher.group(6).trim());
        } else {
            details.put("Previous Insurer Address", "Not Found");
        }

        return details;
    }

    public static Map<String, String> extractPeriodOfInsurance(String text) {
        Map<String, String> details = new LinkedHashMap<>();

        // Define regex patterns for "From" and "To" period of insurance
        String periodRegex = "Period of Insurance From\\s*([\\d/]+)\\s*To\\s*([\\d/]+)";

        // Create a pattern and matcher to find the period
        Pattern pattern = Pattern.compile(periodRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);

        // Check if a match is found
        if (matcher.find()) {
            details.put("Period of Insurance From", matcher.group(1).trim());
            details.put("Period of Insurance To", matcher.group(2).trim());
        } else {
            details.put("Period of Insurance From", "Not Found");
            details.put("Period of Insurance To", "Not Found");
        }

        return details;
    }

    public static Map<String, String> extractRegistrationNumber(String text) {
        Map<String, String> details = new LinkedHashMap<>();

        String registrationNumberRegex = "Registration Number\\s*([A-Za-z]{2}\\d{2}[A-Za-z]{1,2}\\d{1,4})";
        String policyTypeRegex = "\"[A-Za-z ]+\" Policy for [^\\n]+";

        Pattern pattern = Pattern.compile(registrationNumberRegex, Pattern.DOTALL);
        Pattern patterns = Pattern.compile(policyTypeRegex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        Matcher matchers = patterns.matcher(text);
        if (matchers.find()) {
            details.put("Policy Type", matchers.group().trim());
        } else {
            details.put("Policy Type", "Not Found");
        }

        if (matcher.find()) {
            details.put("Registration Number", matcher.group(1).trim());
        } else {
            details.put("Registration Number", "Not Found");
        }

        return details;
    }

    public static Map<String, String> extractPolicyDetailsMobile(String text) {
        Map<String, String> details = new LinkedHashMap<>();
        Map<String, String> patterns = new LinkedHashMap<>();
        patterns.put("Phone", "Phone\\s*Mobile\\s*([\\d*]+)");
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            Pattern pattern = Pattern.compile(entry.getValue(), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                details.put(entry.getKey(), matcher.group(1).trim());
            } else {
                details.put(entry.getKey(), "Not Found");
            }
        }
        return details;
    }

}
