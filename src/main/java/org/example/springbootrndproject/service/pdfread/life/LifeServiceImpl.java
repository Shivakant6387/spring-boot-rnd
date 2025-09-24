package org.example.springbootrndproject.service.pdfread.life;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.springbootrndproject.dto.ResponseDto;
import org.example.springbootrndproject.exception.PdfProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LifeServiceImpl implements LifeService {

    @Override
    public ResponseDto futureGeneraliIndiaLifeInsuranceCompanyLtd(MultipartFile file) {
        ResponseDto response = new ResponseDto();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            text = text.replaceAll("\\s{2,}", " ");
            String mobile = null;
            Matcher mobileMatcher = Pattern.compile("\\b[6-9]\\d{9}\\b").matcher(text);
            if (mobileMatcher.find()) {
                mobile = mobileMatcher.group();
            }
            String productName = null;
            Matcher productMatcher = Pattern.compile("Product Name\\s*:?\\s*(.*?)\\s*Product UIN", Pattern.CASE_INSENSITIVE).matcher(text);
            if (productMatcher.find()) {
                productName = productMatcher.group(1).trim();
            }
            String policyNumber = null;
            Matcher policyMatcher = Pattern.compile("Policy Number\\s*:?\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (policyMatcher.find()) {
                policyNumber = policyMatcher.group(1).trim();
            }
            String intermediaryName = null;
            Matcher intermNameMatcher = Pattern.compile("Agent/Broker/Intermediary Name:?\\s*(.*?)\\s*Code", Pattern.CASE_INSENSITIVE).matcher(text);
            if (intermNameMatcher.find()) {
                intermediaryName = intermNameMatcher.group(1).trim();
            }
            String intermediaryCode = null;
            Matcher intermCodeMatcher = Pattern.compile("Code:?\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (intermCodeMatcher.find()) {
                intermediaryCode = intermCodeMatcher.group(1).trim();
            }
            String intermediaryContactNo = null;
            Matcher intermMobileMatcher = Pattern.compile("Mobile Number:?\\s*([6-9]\\d{9})", Pattern.CASE_INSENSITIVE).matcher(text);
            if (intermMobileMatcher.find()) {
                intermediaryContactNo = intermMobileMatcher.group(1).trim();
            }
            String insurerName = null;
            Matcher insurerMatcher = Pattern.compile("Future Generali India Life Insurance Company Ltd\\.?+", Pattern.CASE_INSENSITIVE).matcher(text);
            if (insurerMatcher.find()) {
                insurerName = insurerMatcher.group().trim();
            }
            String nomineeName = null;
            String nomineeRelationShip = null;
            String nomineeRegex = "(MRS|MR|MS)\\s([A-Z\\s]+)\\s(\\d{2}/\\d{2}/\\d{4})\\s(\\d+)\\s([MF])\\s(\\w+)\\s(.+?)\\s(\\d+\\.\\d+)";
            Matcher nomineeMatcher = Pattern.compile(nomineeRegex).matcher(text);
            while (nomineeMatcher.find()) {
                nomineeName = nomineeMatcher.group(1) + " " + nomineeMatcher.group(2).trim();
                nomineeRelationShip = nomineeMatcher.group(6);
            }
            String amountReceived = null;
            Matcher amountMatcher = Pattern.compile("Amount Received\\s*:?\\s*([\\d,]+(?:\\.\\d{1,2})?)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (amountMatcher.find()) {
                amountReceived = amountMatcher.group(1).trim();
            }
            String totalInstalmentPremium = null;
            Matcher premiumMatcher = Pattern.compile("Total Instalment Premium\\s*:?\\s*([\\d,]+(?:\\.\\d{1,2})?)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (premiumMatcher.find()) {
                totalInstalmentPremium = premiumMatcher.group(1).trim();
            }
            String date = null;
            Matcher dateMatcher = Pattern.compile("Date\\s*:?\\s*(\\d{1,2}\\w*\\s\\w+\\s\\d{4})", Pattern.CASE_INSENSITIVE).matcher(text);
            if (dateMatcher.find()) {
                date = dateMatcher.group(1).trim();
            }
            String name = null;
            Matcher nameMatcher = Pattern.compile("Date\\s*:\\s*.*?\\s([A-Z\\s]+)\\sS/O", Pattern.CASE_INSENSITIVE).matcher(text);
            if (nameMatcher.find()) {
                name = nameMatcher.group(1).trim();
            }
            String fatherAddress = null;
            Matcher addrMatcher = Pattern.compile("S/O\\s+(.*?)(?=(LANDMARK:|Tel|$))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
            if (addrMatcher.find()) {
                fatherAddress = addrMatcher.group(1).trim().replaceAll("\\s+", " "); // normalize spaces
            }
            String landmarkRegex = "LANDMARK:\\s*(.*?)\\s*([A-Z]+,\\s*\\d{6})";
            Matcher matcher = Pattern.compile(landmarkRegex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
            String statePin = null;
            String landmark = null;
            if (matcher.find()) {
                landmark = matcher.group(1).trim();
                statePin = matcher.group(2).trim();
            }
            response.setPolicyHolderAddress(fatherAddress + " " + landmark + statePin);
            response.setPolicyHolderName(name);
            response.setIssueDate(date);
            response.setNetPremium(totalInstalmentPremium);
            response.setTotalAmount(amountReceived);
            response.setNomineeRelationship(nomineeRelationShip);
            response.setNomineeName(nomineeName);
            response.setInsurerName(insurerName);
            response.setMobileNumber(mobile);
            response.setProductName(productName);
            response.setPolicyNumber(policyNumber);
            response.setIntermediaryName(intermediaryName);
            response.setIntermediaryCode(intermediaryCode);
            response.setIntermediaryContactNo(intermediaryContactNo);
            response.setMessage("Fetched data successfully!");
            response.setHttpStatus(HttpStatus.OK.value());
        } catch (IOException e) {
            throw new PdfProcessingException("Failed to read or parse PDF file", e);
        }

        return response;
    }

    @Override
    public ResponseDto pnbMetLife(MultipartFile file) {
        ResponseDto response = new ResponseDto();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            text = text.replaceAll("[^\\x00-\\x7F]", " ");
            text = text.replaceAll("\\s{2,}", " ")
                    .replaceAll("[\\r\\n]+", "\n")
                    .trim();
            String policyHolderName = null;
            Matcher holderMatcher = Pattern.compile("Name of Policyholder\\s*:?\\s*([A-Z. ]+)\\s+Gender", Pattern.CASE_INSENSITIVE).matcher(text);
            if (holderMatcher.find()) {
                policyHolderName = holderMatcher.group(1).trim();
            }
            String policyNumber = null;
            Matcher policyMatcher = Pattern.compile("Policy No\\s*:?\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (policyMatcher.find()) {
                policyNumber = policyMatcher.group(1).trim();
            }
            String mobile = null;
            Matcher mobileMatcher = Pattern.compile("\\b[6-9]\\d{9}\\b").matcher(text);
            if (mobileMatcher.find()) {
                mobile = mobileMatcher.group();
            }
            String productName = null;
            Matcher productMatcher = Pattern.compile("Plan\\s*(PNB MetLife [A-Za-z ]+?)\\s*(?:Dear|Policy|$)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (productMatcher.find()) {
                productName = productMatcher.group(1).trim();
            }
            String planOption = null;
            Matcher planMatcher = Pattern.compile("Plan option\\s*([A-Za-z +]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (planMatcher.find()) {
                planOption = planMatcher.group(1).trim();
            }

            String netPremium = null;
            Matcher netPremMatcher = Pattern.compile("Installment Premium \\(Rs.\\)\\s*([\\d,.]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (netPremMatcher.find()) {
                netPremium = netPremMatcher.group(1).trim();
            }

            String totalPremium = null;
            Matcher totalPremMatcher = Pattern.compile("Total Installment Premium.*?([\\d,.]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (totalPremMatcher.find()) {
                totalPremium = totalPremMatcher.group(1).trim();
            }
            String annualisedPremium = null;
            Matcher annualPremMatcher = Pattern.compile("Annualised Premium \\(Rs\\)\\s*([\\d,.]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (annualPremMatcher.find()) {
                annualisedPremium = annualPremMatcher.group(1).trim();
            }
            String gst = null;
            Matcher gstMatcher = Pattern.compile("Goods and Services Tax \\(Rs\\)\\*?\\s*([\\d,.]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (gstMatcher.find()) {
                gst = gstMatcher.group(1).trim();
            }
            String sumAssured = null;
            Matcher sumMatcher = Pattern.compile("Basic Sum Assured \\(Rs\\.\\)\\s*([\\d,.]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (sumMatcher.find()) {
                sumAssured = sumMatcher.group(1).trim();
            }
            String policyTerm = null;
            Matcher termMatcher = Pattern.compile("Policy term\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (termMatcher.find()) {
                policyTerm = termMatcher.group(1).trim();
            }
            String maturityDate = null;
            Matcher maturityMatcher = Pattern.compile("Maturity Date\\s*(\\d{1,2}/[A-Za-z]+/\\d{4})", Pattern.CASE_INSENSITIVE).matcher(text);
            if (maturityMatcher.find()) {
                maturityDate = maturityMatcher.group(1).trim();
            }
            String intermediaryCode = null;
            Matcher intermCodeMatcher = Pattern.compile("Code\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (intermCodeMatcher.find()) {
                intermediaryCode = intermCodeMatcher.group(1).trim();
            }
            String address = null;
            Matcher addrMatcher = Pattern.compile("Address of Policyholder\\s*(.+?)(?=Telephone|Mobile|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
            if (addrMatcher.find()) {
                address = addrMatcher.group(1)
                        .replaceAll("\\s+", " ")   // collapse spaces/newlines
                        .replaceAll("[^A-Za-z0-9,./\\-\\s]", "") // strip junk
                        .trim();
                address = address.replaceAll("(\\d{6}).*$", "$1");
            }
            String insurerName = null;
            Matcher insurerMatcher = Pattern.compile("PNB MetLife India Insurance Co\\. Ltd", Pattern.CASE_INSENSITIVE).matcher(text);
            if (insurerMatcher.find()) {
                insurerName = insurerMatcher.group().trim();
            }
            response.setInsurerName(insurerName);
            response.setPolicyHolderName(clean(policyHolderName));
            response.setPolicyNumber(clean(policyNumber));
            response.setMobileNumber(clean(mobile));
            response.setProductName(clean(productName));
            response.setNetPremium(clean(netPremium));
            response.setTotalAmount(clean(totalPremium));
            response.setPolicyTerm(clean(policyTerm));
            response.setPolicyExpiryDate(clean(maturityDate));
            response.setIntermediaryCode(clean(intermediaryCode));
            response.setPolicyHolderAddress(clean(address));
            response.setMessage("Fetched data successfully!");
            response.setHttpStatus(HttpStatus.OK.value());

        } catch (IOException e) {
            throw new PdfProcessingException("Failed to read or parse PDF file", e);
        }

        return response;
    }

    @Override
    public ResponseDto pramericaLifeRockSolidFuture(MultipartFile file) {
        ResponseDto response = new ResponseDto();

        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            text = text.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");
            text = text.replaceAll("[\\t ]+", " ");

            String mobile = null;
            Matcher mobileMatcher = Pattern.compile("\\b[6-9]\\d{9}\\b").matcher(text);
            if (mobileMatcher.find()) {
                mobile = mobileMatcher.group();
            }
            String productName = null;
            Matcher productMatcher = Pattern.compile("Pramerica Life RockSolid Future", Pattern.CASE_INSENSITIVE).matcher(text);
            if (productMatcher.find()) {
                productName = productMatcher.group().trim();
            }
            String policyHolderName = null;
            Matcher nameMatcher = Pattern.compile("(Mr|Mrs|Ms)\\.?\\s+([A-Z][a-z]+\\s+[A-Z][a-z]+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (nameMatcher.find()) {
                policyHolderName = nameMatcher.group(0).trim();
            }
            String amountPayable = null;
            Matcher amtMatcher = Pattern.compile("Amount Payable\\s*[:\\-]?\\s*Rs\\.?\\s*([\\d,]+(?:\\.\\d{1,2})?)",
                    Pattern.CASE_INSENSITIVE).matcher(text);
            if (amtMatcher.find()) {
                amountPayable = amtMatcher.group(1).trim();
            }
            String policyTerm = null;
            Matcher termMatcher = Pattern.compile("Policy Term\\s*:?\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(text);
            if (termMatcher.find()) {
                policyTerm = termMatcher.group(1).trim();
            }

            String policyNumber = null;
            Matcher policyMatcher = Pattern.compile("Policy\\s*Number[^0-9]*(\\d{6,12})",
                            Pattern.CASE_INSENSITIVE | Pattern.DOTALL)
                    .matcher(text);
            if (policyMatcher.find()) {
                policyNumber = policyMatcher.group(1).trim();
            }

            String policyHolderAddress = null;

            Matcher addrMatcher = Pattern.compile(
                    "(S/O[\\s\\S]*?\\d{6},\\s*[A-Za-z]+)",
                    Pattern.CASE_INSENSITIVE
            ).matcher(text);

            if (addrMatcher.find()) {
                policyHolderAddress = addrMatcher.group(1)
                        .replaceAll("\\s+", " ")
                        .trim();
            }
            String agentName = findFirstGroup(text, "(?i)Your Agent Name\\s*[-:]\\s*(.*?)(?:,|Code|Email|$)");
            String agentCode = findFirstGroup(text, "(?i)Code\\s*[-–:]\\s*([A-Z0-9]+)");
            String branchPhone = findFirstGroup(text, "(?i)Phone No\\.?\\s*[-:]\\s*(\\d{8,12})");
              if (agentName != null) agentName = agentName.trim();
            String premiumFrequency = findFirstGroup(text, "(?i)Premium\\s*Frequency\\s*(Monthly|Quarterly|Half[- ]?Yearly|Yearly)");
            if (premiumFrequency == null) {
                premiumFrequency = findFirstGroup(text, "&\\s*(Monthly|Quarterly|Half[- ]?Yearly|Yearly)");
            }
            response.setPayMode(premiumFrequency);
            response.setIntermediaryName(agentName);
            response.setIntermediaryCode(agentCode);
            response.setIntermediaryContactNo(branchPhone);
            response.setPolicyHolderAddress(policyHolderAddress);
            response.setPolicyNumber(policyNumber);
            response.setPolicyTerm(policyTerm);
            response.setPolicyHolderName(policyHolderName);
            response.setMobileNumber(mobile);
            response.setProductName(productName);
            response.setTotalAmount(amountPayable);
            response.setInsurerName("Pramerica Life Insurance Ltd.");
            response.setMessage("Fetched data successfully!");
            response.setHttpStatus(HttpStatus.OK.value());

        } catch (IOException e) {
            throw new PdfProcessingException("Failed to read or parse PDF file", e);
        }

        return response;
    }

    private static String findFirstGroup(String text, String regex) {
        try {
            Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(text);
            if (matcher.find()) {
                if (matcher.groupCount() >= 1) {
                    return matcher.group(1).trim();
                } else {
                    return matcher.group().trim();
                }
            }
        } catch (Exception e) {
            // log if needed
        }
        return null;
    }

    private String clean(String input) {
        if (input == null) return null;
        return input.replaceAll("[^\\x00-\\x7F]", "").trim();
    }
}
