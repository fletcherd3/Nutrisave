package com.navbara_pigeons.wasteless.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * User DTO which returns all user information except password
 */
@Data
public class FullUserDto {

  private long id;
  private String firstName;
  private String lastName;
  private String middleName;
  private String nickname;
  private String bio;
  private String email;
  private LocalDate dateOfBirth;
  private String phoneNumber;
  private FullAddressDto homeAddress;
  private ZonedDateTime created;
  private String role;
  @JsonProperty("businessesAdministered")
  private List<BasicBusinessDto> businesses;

  public FullUserDto(User user) {
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.middleName = user.getMiddleName();
    this.nickname = user.getNickname();
    this.bio = user.getBio();
    this.email = user.getEmail();
    this.dateOfBirth = user.getDateOfBirth();
    this.phoneNumber = user.getPhoneNumber();
    this.created = user.getCreated();
    this.role = user.getRole();
    this.homeAddress = new FullAddressDto(user.getHomeAddress());
    if (user.getBusinesses() != null) {
      this.businesses = makeBusinessDto(user.getBusinesses());
    }
  }

  public FullUserDto() {

  }

  private List<BasicBusinessDto> makeBusinessDto(List<Business> businesses) {
    ArrayList<BasicBusinessDto> businessDtos = new ArrayList<BasicBusinessDto>();
    for (Business business : businesses) {
      businessDtos.add(new BasicBusinessDto(business));
    }
    return businessDtos;
  }

}
