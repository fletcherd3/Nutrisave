package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Business DTO which returns all user details except catalog and administrators list
 */
@Data
public class FullBusinessDto {

  private long id;
  private long primaryAdministratorId;
  private String name;
  private String description;
  private FullAddressDto address;
  private String businessType;
  private ZonedDateTime created;
  private List<BasicUserDto> administrators;

  public FullBusinessDto(Business business, String publicPathPrefix) {
    this.id = business.getId();
    this.primaryAdministratorId = business.getPrimaryAdministratorId();
    this.name = business.getName();
    this.description = business.getDescription();
    this.address = new FullAddressDto(business.getAddress());
    this.businessType = business.getBusinessType().toString();
    this.created = business.getCreated();
    if (business.getAdministrators() != null) {
      this.administrators = makeUserDto(business.getAdministrators());
    }
  }

  public FullBusinessDto() {

  }

  private List<BasicUserDto> makeUserDto(List<User> users) {
    ArrayList<BasicUserDto> userlistDto = new ArrayList<>();
    for (User user : users) {
      BasicUserDto userDto = new BasicUserDto(user);
      userDto.setBusinesses(new ArrayList<>());
      userlistDto.add(userDto);
    }
    return userlistDto;
  }
}
