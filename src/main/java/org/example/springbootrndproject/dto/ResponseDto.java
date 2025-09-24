package org.example.springbootrndproject.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    private String policyNumber;
    private String policyHolderName;
    private String mobileNumber;
    private String message;
    private String productName;
    private String policyExpiryDate;
    private String policyTerm;
    private String totalAmount;
    private String netPremium;
    private String policyHolderAddress;
    private String insurerName;
    private String nomineeRelationship;
    private String nomineeName;
    private String issueDate;
    private String intermediaryName;
    private String intermediaryCode;
    private String intermediaryContactNo;
    private String payMode;
    private int httpStatus;
}
