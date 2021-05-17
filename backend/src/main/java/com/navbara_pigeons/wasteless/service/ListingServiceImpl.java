package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.validation.ListingServiceValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class for dealing with all business logic to do with listings
 */
@Service
public class ListingServiceImpl implements ListingService {

  private final UserService userService;
  private final BusinessService businessService;
  private final ListingDao listingDao;

  /**
   * ListingService constructor that takes autowired parameters and sets up the service for
   * interacting with all listing related services.
   */
  @Autowired
  public ListingServiceImpl(UserService userService, BusinessService businessService,
      ListingDao listingDao) {
    this.userService = userService;
    this.businessService = businessService;
    this.listingDao = listingDao;
  }

  /**
   * Adds a given listing to a businesses listings
   *
   * @param businessId id of the business to add the listing to
   * @param listing    listing to be added tot the business
   * @return newly created listing id
   * @throws ForbiddenException        when a user is not admin nor business admin
   * @throws BusinessNotFoundException when no business with given id exists
   * @throws UserNotFoundException     this will be caught by spring first
   */
  public Long addListing(long businessId, Listing listing)
      throws ForbiddenException, BusinessNotFoundException, UserNotFoundException, ListingValidationException {
    if (!userService.isAdmin() && !businessService.isBusinessAdmin(businessId)) {
      throw new ForbiddenException(
          "Only admins and business admins are allowed to add listings to a business");
    }
    if (listing.getCloses() == null) {
      listing.setCloses(listing.getInventoryItem().getExpires());
    }
    if (!ListingServiceValidation.isListingValid(listing)) {
      throw new ListingValidationException();
    }
    listingDao.saveListing(listing);
    return listing.getId();
  }
}