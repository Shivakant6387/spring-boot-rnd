package org.example.springbootrndproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressesDto {

	private Long addressId;

	private Long proposalId;

	private String addressType;

	private String address1;

	private String address2;

	private String address3;

	private int stateId;

	private String stateName;

	private int cityId;

	private String cityName;

	private int pinCode;

	private String VillageSectorName;
}
