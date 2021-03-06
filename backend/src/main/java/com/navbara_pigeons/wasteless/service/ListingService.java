package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.dto.TransactionDto;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessAndListingMismatchException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryUpdateException;
import com.navbara_pigeons.wasteless.exception.ListingNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;

import com.navbara_pigeons.wasteless.model.ListingsSearchParams;
import java.time.LocalDate;
import java.util.List;

public interface ListingService {

  Long addListing(long businessId, long inventoryItemId, Listing listing)
      throws BusinessNotFoundException, UserNotFoundException, ListingValidationException, InsufficientPrivilegesException, InventoryItemNotFoundException;

  Listing getListing(long listingId) throws ListingNotFoundException;

  PaginationDto<FullListingDto> getListings(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, ListingSortByOption sortBy, boolean isAscending)
      throws BusinessNotFoundException, UserNotFoundException, InvalidPaginationInputException;

  PaginationDto<FullListingDto> searchListings(ListingsSearchParams params);


  void deleteListing(Long listingId);

  TransactionDto purchaseListing(long businessId, long listingId)
      throws InventoryItemNotFoundException, BusinessNotFoundException, InventoryUpdateException, BusinessAndListingMismatchException, ListingNotFoundException;
}