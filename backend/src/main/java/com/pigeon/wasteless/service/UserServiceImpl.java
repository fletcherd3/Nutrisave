package com.pigeon.wasteless.service;

import static com.pigeon.wasteless.validation.UserServiceValidation.isDobValid;
import static com.pigeon.wasteless.validation.UserServiceValidation.isEmailValid;
import static com.pigeon.wasteless.validation.UserServiceValidation.isPasswordValid;
import static com.pigeon.wasteless.validation.UserServiceValidation.isUserValid;

import com.pigeon.wasteless.dao.UserDao;
import com.pigeon.wasteless.entity.User;
import com.pigeon.wasteless.exception.UserAlreadyExistsException;
import com.pigeon.wasteless.exception.UserNotFoundException;
import com.pigeon.wasteless.exception.UserRegistrationException;
import com.pigeon.wasteless.security.model.BasicUserDetails;
import com.pigeon.wasteless.security.model.UserCredentials;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.InvalidAttributeValueException;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * A UserService implementation.
 */
@Service
public class UserServiceImpl implements UserService {

  private final UserDao userDao;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final BCryptPasswordEncoder encoder;

  /**
   * UserServiceImplementation constructor that takes autowired parameters and sets up the service
   * for interacting with all user related services.
   *
   * @param userDao                      The UserDataAccessObject.
   * @param authenticationManagerBuilder The global AuthenticationManagerBuilder.
   * @param encoder                      Password encoder.
   */
  @Autowired
  public UserServiceImpl(UserDao userDao,
      AuthenticationManagerBuilder authenticationManagerBuilder, BCryptPasswordEncoder encoder) {
    this.userDao = userDao;
    this.authenticationManagerBuilder = authenticationManagerBuilder;
    this.encoder = encoder;
  }

  /**
   * Performs basic business checks, sets role, created date and hashes password before sending to
   * the dao
   *
   * @param user User object to be saved.
   * @throws UserAlreadyExistsException Thrown when a user already exists in the database
   * @throws UserRegistrationException  Thrown when an invalid dob is received
   */
  @Override
  @Transactional
  public JSONObject saveUser(User user)
      throws UserAlreadyExistsException, UserRegistrationException, UserNotFoundException {
    // Email validation
    if (!isEmailValid(user.getEmail())) {
      throw new UserRegistrationException("Invalid email");
    }
    if (userDao.userExists(user.getEmail())) {
      throw new UserAlreadyExistsException("User already exists");
    }
    // Validates the users DOB
    if (!isDobValid(user.getDateOfBirth())) {
      throw new UserRegistrationException("Invalid date of birth");
    }
    // Check user is over 13 years old
    if (!LocalDate.parse(user.getDateOfBirth()).isBefore(LocalDate.now().minusYears(13))) {
      throw new UserRegistrationException("Must be 13 years or older to register");
    }
    user.setDateOfBirth(
        LocalDate.parse(user.getDateOfBirth()).format(DateTimeFormatter.ISO_LOCAL_DATE));

    // Password and field validation
    if (!isPasswordValid(user.getPassword())) {
      throw new UserRegistrationException("Password does not pass validation check");
    }
    if (!isUserValid(user)) {
      throw new UserRegistrationException("Required user fields cannot be null");
    }

    // Set user credentials for logging in after registering
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail(user.getEmail());
    userCredentials.setPassword(user.getPassword());
    System.out.println("Here");
    // Set created date, hash password
    user.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    user.setPassword(encoder.encode(user.getPassword()));
    user.setRole("ROLE_USER");
    this.userDao.saveUser(user);

    // Logging in the user and returning the id
    JSONObject response = login(userCredentials);
    return response;
  }

  /**
   * Calls the userDao to get the specified user
   *
   * @param id the id of the user
   * @return the User instance of the user
   */
  @Override
  public JSONObject getUserById(long id) throws UserNotFoundException {
    // TODO change this implementation to just hide the users password instead of creating a new JSONObject
    User user = this.userDao.getUserById(id);
    JSONObject response = new JSONObject();
    response.put("id", Long.toString(id));
    response.put("firstName", user.getFirstName());
    response.put("lastName", user.getLastName());
    response.put("middleName", user.getMiddleName());
    response.put("nickname", user.getNickname());
    response.put("bio", user.getBio());
    response.put("email", user.getEmail());
    response.put("dateOfBirth", user.getDateOfBirth());
    response.put("phoneNumber", user.getPhoneNumber());
    response.put("homeAddress", user.getHomeAddress());
    response.put("created", user.getCreated());
    response.put("role", user.getRole());
    return response;
  }

  /**
   * Login business logic. This method gets UserCredentials from the endpoint and attempts to
   * authenticate the user. Authentication is done through the global AuthenticationManagerBuilder.
   *
   * @param userCredentials The UserCredentials object storing the email and password.
   * @throws AuthenticationException An authentication exception that is assigned HTTP error codes
   *                                 at the controller.
   */
  @Override
  public JSONObject login(UserCredentials userCredentials)
      throws AuthenticationException, UserNotFoundException {
    // Check for null in userCredentials
    if (userCredentials.getEmail() == null || userCredentials.getPassword() == null) {
      throw new BadCredentialsException("No username/password supplied.");
    }
    // Create authentication token
    UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(
        userCredentials.getEmail(),
        userCredentials.getPassword()
    );
    // Perform authentication and get authentication object
    Authentication auth = authenticationManagerBuilder.getOrBuild().authenticate(authReq);
    // Set current security context with authentication
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(auth);
    // Build and return JSON response
    JSONObject response = new JSONObject();
    User user = userDao.getUserByEmail(userCredentials.getEmail());
    response.put("userId", user.getId());
    return response;
  }

  /**
   * This method searches for a user using a given email address.
   *
   * @param email A unique email address
   * @return Returns the user with the corresponding email address.
   */
  @Override
  public User getUserByEmail(String email) throws UserNotFoundException {
    return userDao.getUserByEmail(email);
  }

  /**
   * This method is used to get a list of all users (use with care as it may impact performace
   * significantly.
   *
   * @return Returns a list of all users.
   */
  @Override
  public List<User> getAllUsers() {
    return null;
  }

  /**
   * Calls the userDao to search for users using the given username
   *
   * @param searchQuery the name being searched for
   * @return A list containing all the users whose names/nickname match the username
   */
  @Override
  public List<User> searchUsers(String searchQuery) throws InvalidAttributeValueException {
    return userDao.searchUsers(searchQuery);
  }

  /**
   * This method gives administrator privileges to another user if authorised.
   *
   * @param id the id of the User to be made Admin.
   * @throws UserNotFoundException   The user to be made admin was not found.
   * @throws BadCredentialsException The requester is not an Admin.
   */
  @Override
  @Transactional
  public void makeUserAdmin(long id) throws UserNotFoundException, BadCredentialsException {
    // Check if the requester have sufficient privileges
    if (!isAdmin()) {
      throw new BadCredentialsException("Insufficient privileges");
    }
    // Get the user to me made admin
    User userToBeAdmin = userDao.getUserById(id);
    // Add GAA role to user
    userToBeAdmin.makeAdmin();
    userDao.saveUser(userToBeAdmin);
  }

  /**
   * This method revokes the admin privileges of the user with the given id. If that user is
   * currently logged in, it resets their authentication to reflect the new roles.
   *
   * @param id The ID of the user who will lose admin privileges.
   * @throws UserNotFoundException The exception thrown if the user is not in the database.
   */
  @Override
  @Transactional
  public void revokeAdmin(long id) throws UserNotFoundException {
    // Test if current user is admin
    if (!isAdmin()) {
      throw new BadCredentialsException("Insufficient privileges");
    }
    // Get the user
    User user = userDao.getUserById(id);
    // Remove the admin role if it is present
    user.revokeAdmin();
    // Update the database with the updated user
    userDao.saveUser(user);
    // Check if the revoked user is the current user
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth.getName().equalsIgnoreCase(user.getEmail())) {
      // Update the current security context with the updated authorizations
      UserDetails userDetails = new BasicUserDetails(user);
      Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(),
          auth.getCredentials(), userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
  }

  /**
   * This helper method tests whether the current user has the ADMIN role
   *
   * @return true if user is admin.
   */
  public boolean isAdmin() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    for (GrantedAuthority simpleGrantedAuthority : authentication.getAuthorities()) {
      if (simpleGrantedAuthority.getAuthority().contains("ADMIN")) {
        return true;
      }
    }
    return false;
  }
}
