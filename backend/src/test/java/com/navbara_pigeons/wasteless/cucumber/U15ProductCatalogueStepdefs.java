package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.service.BusinessService;
import com.navbara_pigeons.wasteless.service.UserService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U15ProductCatalogueStepdefs extends CucumberTestProvider {

  private MvcResult mvcResult;
  private User user;

  @Given("a user with name {string} exists and already administers a business called {string} that sells a product {string}")
  public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String userName, String businessName, String productName) throws Exception {
    String email = userName + "@example.com";
    String password = "password123";
    User user = this.makeUser(email, password, true);
    Business business = makeBusiness(businessName, user);
    user.setPassword(password);
    user.setFirstName(userName);
    UserCredentials credentials = new UserCredentials();
    credentials.setEmail(email);
    credentials.setPassword(password);
    Assertions.assertDoesNotThrow(() -> userController.registerUser(user));
    Assertions.assertDoesNotThrow(() -> businessController.registerBusiness(new CreateBusinessDto(business)));
  }

  @When("the user with email address {string} and password {string} logs in and requests his product catalogue with business id {string}")
  public void requestsHisProductCatalogue(String userEmail, String password, String Businessid) throws Exception {
    JSONObject credentials = new JSONObject();
    credentials.put("email", userEmail);
    credentials.put("password", password);
    MvcResult mvcResult = (MvcResult) mockMvc.perform(
            post("/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(credentials.toString())
                    .accept(MediaType.ALL))
            .andExpect(status().is(200));
    String userId = mvcResult.getResponse().getContentAsString().replaceAll("[^0-9]", "");
    User user = (User) mockMvc.perform(
            get("/user/" + userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().is(200));
    Business business = user.getBusinesses().get(0);
    Long id = business.getId();
    ResultActions product = mockMvc.perform(
            get("/businesses/" + id + "/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.ALL))
            .andExpect(status().is(200));
  };


  @Then("The product {string} is displayed")
  public void theProductIsDisplayed(String arg0) {

  }

  @When("{string} tries to access another business called {string} product {string}")
  public void triesToAccessAnotherBusinessCalledProduct(String arg0, String arg1, String arg2) {
  }

  @Then("the product {string} is not displayed")
  public void theProductIsNotDisplayed(String arg0) {
  }

  @Given("a user with name {string} has a {string} business {string} in {string}")
  public void aUserWithNameHasABusinessIn(String arg0, String arg1, String arg2, String arg3) {
  }

  @When("{string} creates a product {string} to sell at business {string}")
  public void createsAProductToSellAtBusiness(String arg0, String arg1, String arg2) {
  }

  @When("{string} creates a product {string} made by {string} with RRP {string}")
  public void createsAProductMadeByWithRRP(String arg0, String arg1, String arg2, String arg3) {
  }

  @Then("the ID, date created is set automatically and the currency is set to {string}")
  public void theIDDateCreatedIsSetAutomaticallyAndTheCurrencyIsSetTo(String arg0) {
  }
}
