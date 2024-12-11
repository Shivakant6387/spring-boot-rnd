package org.example.springbootrndproject.entity;


import java.io.Serializable;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proposal_crm")
public class Proposal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long proposalId;
    private String companyName;
    private String fullNameAsRC;
    private String mobileNo;
    private String emailId;
    private String make;
    private String model;
    private String variant;
    private String vehicleCc;
    private String rtoName;
    private String vehicleRegistertionNo;
    private String engineNumber;
    private String chassisNumber;
    private String previousPolicyNumber;
    private String basicOD;
    private String basicTP;
    private String netPremium;
    private String totalPremium;
    private String currentNCB;
    private String previousNCB;
    private String manufactureYear;
    private String newPolicyStartDate;
    private String newPolicyExpireDate;
    private String previousYearPolicyExpiryDate;
    private String policyNumber;
    private String seatingCapacity;
    private String message;
    private int httpStatus;
    private String addresses;
    private String currentPolicyType;
}
