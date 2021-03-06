package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import java.time.ZonedDateTime;
import lombok.Data;

/**
 * Business DTO which returns all user details except catalog, administrators list and image list
 */
@Data
public class BasicBusinessDto {

  private long id;
  private long primaryAdministratorId;
  private String name;
  private String description;
  private FullAddressDto address;
  private String businessType;
  private ZonedDateTime created;

  /**
   * Constructor for creating public (basic address) Business DTO from Business Entity.
   *
   * @param business
   */
  public BasicBusinessDto(Business business) {
    this.id = business.getId();
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new FullAddressDto(business.getAddress());
    this.businessType = business.getBusinessType().toString();
    this.created = business.getCreated();
  }

  public BasicBusinessDto() {

  }

}
