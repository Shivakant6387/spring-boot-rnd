package org.example.springbootrndproject.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ProposalRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String leadEnquiryNo;
    private String urlName;
    private int userId;
    private int roleId;
    private int varientId;
    private int productId;
    private long leadId;
    private long proposalId;
    private int insurerId;
    private long quoteId;
    private String quoteRequestDetails;
    private String proposalEnqiryNo;
    private String companyName;
    private String companyGstNo;
    private String fullNameAsRC;
    private String fatherName;
    private String mobileNo;
    private String emailId;
    private String gender;
    private String materialStatus;
    private Date dob;
    private int occupationId;
    private String occupationName;
    private String nomineeName;
    private int relationshipId;
    private String relationshipName;
    private int nomineeAge;
    private boolean isChkFinance;
    private int financerId;
    private String financeType;
    private String financerName;
    private String financeBranchName;
    private int rtoId;
    private String rtoCode;
    private String vehicleRegistertionNo;
    private Date vehicleRegistertionDate;
    private String EngineNumber;
    private String ChassisNumber;
    private int prevInsurerId;
    private int prevTPInsurerId;
    private boolean isKycDone;
    private String PreviousPolicyNumber;
    private String panNo;
    private long coverIdvAmt;
    private double premiumAmt;
    private double gstAmt;
    private double totalPremiumAmt;
    private Date policyStartDate;
    private Date policyEndDate;
    private String currentPolicyType; // pass value comprehensive, OD, TP
    private String kycReferenceDocId;
    private String kycReferenceNumber;
    private String ckycId;
    private Date previousPolicyTPEndDate; // use for HDFC Ergo
    private Date previousPolicyTPSartDate; // use for HDFC Ergo
    private String previousPolicyTPInsurer; // use for HDFC Ergo
    private String previousPolicyTPPolicyNo; // use for HDFC Ergo
    private String status;
    private String applicationId;
    private String puccFlag; // Pollution Under Control Certificate // Pass value YN
    private String puccNo;
    private String puccStateName;
    private Date puccValidToDate;
    private Date puccValidFromDate;
    private String commonValue1;
    private String commonValue2;
    private String commonValue3;
    private String commonValue4;
    private String commonValue5;
    private String commonValue6; //user for icici
    private String commonValue7; //user for icici
    private String chkCarRegisteredCompany;
    private String bharatSeriesNoFlag; // pass value Y/N
    private ArrayList<AddressesDto> addresses;
    private String redirectBaseUrlName;
    private String financerAddress;
    private String financerState;
    private String financerCity;
}

