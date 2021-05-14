package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

public class InventoryControllerTest extends ControllerTestProvider {

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsAdmin() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsWrongUser() throws Exception {
    String endpointUrl = "/businesses/3/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(403));
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneNonExistingBusinessTest() throws Exception {
    String endpointUrl = "/businesses/9999/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(406));
  }

  @Test
  @WithAnonymousUser
  void getInventoryFromOneBusinessTestAsAnon() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void getInventoryFromOneBusinessTestInvalidId() throws Exception {
    String endpointUrl = "/businesses/-1/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(406));
  }

  @Test
  @WithMockUser
  void asUser_addInventoryItem_expectOk() {
    // TODO
  }

  @Test
  @WithAnonymousUser
  void asAnon_addInventoryItem_expectForbidden() {
    // TODO
  }

  @Test
  @WithMockUser
  void asUser_addInventoryItemToNonExistingBusiness_expectNotFound() {
    // TODO
  }

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void asSpecificUser_addInventoryItemToOtherUsersBusiness_expectForbidden() {
    //TODO
  }

}
