package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.UserSortByOption;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.NotAcceptableException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserAuthenticationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import javax.management.InvalidAttributeValueException;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

public interface UserService {

  JSONObject saveUser(User user)
      throws UserAlreadyExistsException, UserRegistrationException, UserNotFoundException, AddressValidationException, UserAuthenticationException;

  JSONObject login(UserCredentials userCredentials)
      throws AuthenticationException, UserNotFoundException, UserAuthenticationException;

  User getUserById(long id) throws UserNotFoundException;

  User getUserByEmail(String email) throws UserNotFoundException;

  void revokeAdmin(long id) throws UserNotFoundException, NotAcceptableException;

  PaginationDto<BasicUserDto> searchUsers(String searchQuery, Integer pagStartIndex,
      Integer pagEndIndex, UserSortByOption sortBy, boolean isAscending)
      throws InvalidAttributeValueException, InvalidPaginationInputException;

  void makeUserAdmin(long id) throws UserNotFoundException, BadCredentialsException;

  boolean isAdmin();

  boolean isSelf(String userEmail);

  String getLoggedInUserEmail();

  User getLoggedInUser() throws UserNotFoundException;
}
