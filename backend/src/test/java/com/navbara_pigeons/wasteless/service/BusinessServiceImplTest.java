package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
public class BusinessServiceImplTest {

  @Autowired
  UserController userController;
  @Autowired
  BusinessDao businessDao;
  @Autowired
  AddressDao addressDao;
  @Autowired
  BusinessService businessService;
  @Autowired
  UserService userService;

  @Test
  void saveInvalidBusiness() {
    // Test invalid businessType fields
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    try {
      userService.login(userCredentials);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String[] businessTypeTests = {"asd", "123", "Marketing", "Retail", "Service"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      assertThrows(Exception.class, () -> businessService.saveBusiness(testBusiness));
    }
  }

  @Test
  void saveValidBusiness() {
    // Test valid businessType fields
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    try {
      userService.login(userCredentials);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String[] businessTypeTests = {"Accommodation and Food Services", "Retail Trade",
        "Charitable organisation", "Non-profit organisation"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    }
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void getBusinessByIdExpectOk() {
    Business testBusiness = makeBusiness();
    Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    Assertions.assertDoesNotThrow(() -> businessService.getBusinessById(testBusiness.getId(), true));
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void getBusinessByIdExpectAdminsHidden() {
    Business testBusiness = makeBusiness();
    Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    JSONObject newBusiness = null;
    try {
      // includeAdmins set to false, assuming no admins are returned in response
      newBusiness = businessService.getBusinessById(testBusiness.getId(), false);
    } catch (BusinessNotFoundException e) {
      assert(false);
    } catch (UnhandledException e) {
      assert(false);
    }
    assertFalse(newBusiness.containsKey("administrators"));
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void getBusinessByIdCheckSensitiveFieldsHidden() {
    // Check sensitive fields are not shown to "dnb36@uclive.ac.nz" (not admin or GAA)
    Business testBusiness = makeBusiness();
    Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));

    // Logging in as "dnb36@uclive.ac.nz"
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    Assertions.assertDoesNotThrow(() -> userService.login(userCredentials));

    // Getting new business for user "dnb36@uclive.ac.nz"
    JSONObject newBusiness = null;
    try {
      newBusiness = businessService.getBusinessById(testBusiness.getId(), true);
    } catch (BusinessNotFoundException e) {
      assert(false);
    } catch (UnhandledException e) {
      assert(false);
    }

    // Administrators should only have one admin "amf133@uclive.ac.nz"
    assertFalse(newBusiness.get("administrators").toString().contains("\"password\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"email\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"dateOfBirth\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"phoneNumber\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"streetNumber\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"streetName\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"postcode\""));
  }

  Address makeAddress() {
    Address address = new Address();
    address.setStreetNumber("3/24")
        .setStreetName("Ilam Road")
        .setPostcode("90210")
        .setCity("Christchurch")
        .setRegion("Canterbury")
        .setCountry("New Zealand");
    addressDao.saveAddress(address);
    return address;
  }

  Business makeBusiness() {
    Business business = new Business();
    business.setName("test")
        .setCreated("2020-07-14T14:32:00Z")
        .setBusinessType("Non-profit organisation")
        .setAddress(makeAddress())
        .setId(0)
        .setDescription("some description");
    return business;
  }
}
