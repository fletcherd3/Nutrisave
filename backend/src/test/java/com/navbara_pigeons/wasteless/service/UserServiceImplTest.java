package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;

import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("ALL")
@SpringBootTest
class UserServiceImplTest extends ServiceTestProvider {

  private final String password = "pass";
  private final String email = "test@example.com";

  @Autowired
  UserController userController;

  @Autowired
  UserDao userDao;

  @Autowired
  AddressDao addressDao;

  void actuallySaveUser(User user) {
    this.addressDao.saveAddress(user.getHomeAddress());
    this.userDao.saveUser(user);
  }

  void actuallySaveAddress(Address address) {
    this.addressDao.saveAddress(address);
  }

  void actuallyDeleteUser(User user) {
    this.userDao.deleteUser(user);
    this.addressDao.deleteAddress(user.getHomeAddress());
  }

  void actuallyDeleteAddress(Address address) {
    this.addressDao.deleteAddress(address);
  }


  /**
   * Tries to save a user, expecting an Exception to be thrown
   * @param user
   * @throws Throwable
   */
  void trySaveUserExpectError(User user) throws Throwable {
    trySaveUserExpectError(user, Exception.class);
  }

  /**
   * Tries to save a user, expecting an exception of a specific class to be thrown
   * @param user
   * @param exceptionType
   * @throws Throwable
   */
  void trySaveUserExpectError(User user, Class exceptionType) throws Throwable {
    try {
      assertThrows(exceptionType, () -> userService.saveUser(user));
    } catch (Throwable throwable) {
      actuallyDeleteUser(user);
      throw throwable;
    }
  }

  @Test
  @Transactional
  void saveValidUser() {
    // Create test user, ensure no errors are thrown
    User testUser = makeUser();
    actuallySaveAddress(testUser.getHomeAddress());
    testUser.setEmail("test@gmail.com");

    try {
      Assertions.assertDoesNotThrow(() -> actuallySaveUser(testUser));
    } finally {
      actuallyDeleteAddress(testUser.getHomeAddress());
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void saveInvalidUserDoB() throws Throwable {
    // Test invalid dob fields
    String[] testValues = {"31-Dec-2000", "dfs", "2020/10/05", "20/1/1"};

    User user = makeUser();

    for (String testValue : testValues) {
      user.setDateOfBirth(testValue);
      trySaveUserExpectError(user);
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void saveInvalidUserEmail() throws Throwable {
    // Test invalid email address
    String[] testValues = {"alec", "alec@", "alec@.", "alec@gmail", "alec@gmail.", "@", "@gmail",
        "@gmail.com"};
    User user = makeUser();

    for (String testValue : testValues) {
      user.setEmail(testValue);
      trySaveUserExpectError(user);
    }
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void saveInvalidUserPassword() throws Throwable {
    // Test invalid passwords
    String[] testValues = {"pwrd", "", "password",
        "passw rd", "pasWrd", "passwoRd", "passwo8d",
        "PASSW8RD"};

    User user = makeUser();

    for (String testValue : testValues) {
      user.setPassword(testValue);
      System.out.println(testValue);
      trySaveUserExpectError(user);
    }
  }

  @Test
  @Transactional
  void saveInvalidCountry() throws Throwable {
    User user = makeUser();
    user.getHomeAddress().setCountry("Fake Zealand");
    trySaveUserExpectError(user);
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  public void getUserSelfCheckFullDetails() throws UserNotFoundException, UnhandledException {
    // Check all fields are returned to the user
    User testUser = makeUser("getUserSelfCheckFullDetails@uclive.ac.nz", "fun123", true);
    Assertions.assertDoesNotThrow(() -> userService.saveUser(testUser));

    // Will fail test if cannot cast to FullUserDto (this happend when BasicUserDto is returned)
    try {
      FullUserDto newUser = (FullUserDto) userService.getUserById(testUser.getId());
    } catch (ClassCastException e) {
      assert(false);
    }
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  public void getUserOtherCheckbasicDetails() throws UserNotFoundException, UnhandledException {
    // Check private fields are hidden from other users
    User testUser = makeUser("getUserOtherCheckbasicDetails@uclive.ac.nz", "fun123", true);
    Assertions.assertDoesNotThrow(() -> userService.saveUser(testUser));

    // Logging in as "dnb36@uclive.ac.nz"
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    Assertions.assertDoesNotThrow(() -> userService.login(userCredentials));

    // Will fail test if cannot cast to BasicUserDto (this happend when FullUserDto is returned)
    try {
      BasicUserDto newUser = (BasicUserDto) userService.getUserById(testUser.getId());
    } catch (ClassCastException e) {
      assert(false);
    }
  }

  /**
   * Gets a user using credentials
   *
   * @param email    email of authorizer
   * @param password password of authorizer
   * @param id       id of user to get
   * @return user with id `id` as JSONObject
   * @throws UserNotFoundException
   */
  private Object getUserAsUser(String email, String password, long id)
      throws UserNotFoundException, UnhandledException {
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail(email);
    userCredentials.setPassword(password);
    userController.login(userCredentials);

    return userService.getUserById(id);
  }

  void assertUserWithJson(User user, JSONObject response, boolean publicOnly) {
    assertEquals(user.getFirstName(), response.getAsString("firstName"));
    assertEquals(user.getLastName(), response.getAsString("lastName"));
    assertEquals(user.getNickname(), response.getAsString("nickname"));

    System.out.println(response.getAsString("created"));

    // TODO created dates
//    Assertions.assertEquals(expect.getCreated(), response.getAsString("created"));
    assertEquals(user.getMiddleName(), response.getAsString("middleName"));
    assertEquals(user.getRole(), response.getAsString("role"));

    if (!publicOnly) {
      assertEquals(user.getEmail(), response.getAsString("email"));
      assertEquals(user.getDateOfBirth(), response.getAsString("dateOfBirth"));
      assertEquals(user.getPhoneNumber(), response.getAsString("phoneNumber"));
    } else {
      System.out.println("!");
      System.out.println(response.getAsString("email"));
      Assertions.assertNull(response.get("email"));
      Assertions.assertNull(response.get("dateOfBirth"));
      Assertions.assertNull(response.get("phoneNumber"));
    }

    Address expectAddress = user.getHomeAddress();
    JSONObject responseAddress = (JSONObject) response.get("homeAddress");

    if (!publicOnly) {
      assertEquals(expectAddress.getStreetNumber(), responseAddress.getAsString("streetNumber"));
      assertEquals(expectAddress.getStreetName(), responseAddress.getAsString("streetName"));
      assertEquals(expectAddress.getPostcode(), responseAddress.getAsString("postcode"));
    } else {
      Assertions.assertNull(responseAddress.get("streetNumber"));
      Assertions.assertNull(responseAddress.get("streetName"));
      Assertions.assertNull(responseAddress.get("postcode"));
    }

    assertEquals(expectAddress.getCity(), responseAddress.getAsString("city"));
    assertEquals(expectAddress.getRegion(), responseAddress.getAsString("region"));
    assertEquals(expectAddress.getCountry(), responseAddress.getAsString("country"));
  }

  @Test
  @Transactional
  void login() {
    // Check that no credentials or incorrect credentials throw an error.
    assertThrows(Exception.class, () -> userController.login(new UserCredentials()));

    // Set up a valid user and credentials
    // Make a user and save with encoded password
    User testUser = makeUser("test@test.com", password, false);

    // -- Set up credentials
    UserCredentials testCredentials = new UserCredentials();
    testCredentials.setEmail(testUser.getEmail());
    testCredentials.setPassword(password);

    actuallySaveUser(testUser);

    // Check that login gives a response.
    assertNotNull(userController.login(testCredentials));
    // Check that no error is thrown.
    assertDoesNotThrow(() -> {
      userController.login(testCredentials);
    });
    // Check that the response is the correct JSONObject
    JSONObject properResponse = new JSONObject();
    try {
      properResponse.put("userId", userDao.getUserByEmail(testCredentials.getEmail()).getId());
      assertEquals(properResponse, userController.login(testCredentials).getBody());
    } catch (UserNotFoundException e) {
    }

  }

  @Test
  @Transactional
  void makeAdminTest() {
    // Make two users (admin/non-admin)
    User adminUser = makeUser("admin@test.com", password, true);
    User toBeAdminUser = makeUser("toBeAdmin@test.com", password, false);

    // Persist users
    actuallySaveUser(adminUser);
    actuallySaveUser(toBeAdminUser);

    // Get users from db
    UserCredentials userCredentials = new UserCredentials();
    try {
      adminUser = userService.getUserByEmail(adminUser.getEmail());
      toBeAdminUser = userService.getUserByEmail(toBeAdminUser.getEmail());

      // Log admin user in
      userCredentials.setEmail(adminUser.getEmail());
      userCredentials.setPassword(password);
      userController.login(userCredentials);

      // Give admin permission to user
      long toBeAdminId = toBeAdminUser.getId();
      assertDoesNotThrow(() -> userService.makeUserAdmin(toBeAdminId));

      // Refresh the user and test for admin permission
      toBeAdminUser = userService.getUserByEmail(toBeAdminUser.getEmail());
      assertEquals("ROLE_ADMIN", toBeAdminUser.getRole());

      // Log in as revokee and test exceptions
      userCredentials.setEmail("toBeAdmin@test.com");
      userCredentials.setPassword(password);
      userController.login(userCredentials);

      // Test for green flow
      assertDoesNotThrow(() -> userController.revokeAdminPermissions(toBeAdminId));
    } catch (UserNotFoundException e) {
      System.out.println("EXPECTED ERROR");
    } finally {
      actuallyDeleteUser(adminUser);
      actuallyDeleteUser(toBeAdminUser);
    }
  }

  @Test
  @Transactional
  void revokeAdminTest() {
    // Make two users (revoker/revokee)
    User revokerUser = makeUser();
    User revokeeUser = makeUser();
    // Give admin roles
    revokerUser.setRole("ROLE_ADMIN");
    revokerUser.setEmail("revoker@test");
    revokerUser.setPassword(encodePass(password));
    revokeeUser.setRole("ROLE_ADMIN");
    revokeeUser.setEmail("revokee@test");
    revokeeUser.setPassword(encodePass(password));
    // Persist users
    actuallySaveUser(revokerUser);
    actuallySaveUser(revokeeUser);
    // Get users from db
    UserCredentials userCredentials = new UserCredentials();
    try {
      revokerUser = userService.getUserByEmail("revoker@test");
      revokeeUser = userService.getUserByEmail("revokee@test");
      System.out.println("REVOKER USER: " + revokerUser.getId());
      System.out.println("REVOKEE USER: " + revokeeUser.getId());

      // Log admin user in
      userCredentials.setEmail("revoker@test");
      userCredentials.setPassword(password);
      userController.login(userCredentials);

      // Revoke revokee admin permission
      long revokeeId = revokeeUser.getId();
      assertDoesNotThrow(() -> userService.revokeAdmin(revokeeId));

      // Refresh the user and test for admin permission
      revokeeUser = userService.getUserByEmail("revokee@test");
      assertFalse(revokeeUser.getRole().contains("ROLE_ADMIN"));

      // Log in as revokee and test exceptions
      userCredentials.setEmail("revokee@test");
      userCredentials.setPassword(password);
      userController.login(userCredentials);

      // Test for permission denied (as revokee is no longer admin)
      long revokerId = revokerUser.getId();
      assertThrows(Exception.class,
          () -> userController.revokeAdminPermissions(revokerId));

    } catch (UserNotFoundException e) {
      Assertions.fail();
    } finally {
      actuallyDeleteUser(revokerUser);
      actuallyDeleteUser(revokeeUser);
    }
  }

  String encodePass(String password) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    return bCryptPasswordEncoder.encode(password);
  }


  /**
   * Makes a non-admin user with default email/password
   *
   * @return
   */
  User makeUser() {
    return makeUser(email, password, false);
  }
}
