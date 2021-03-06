package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.FullInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.enums.InventorySortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import com.navbara_pigeons.wasteless.exception.InventoryUpdateException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import javax.management.InvalidAttributeValueException;

public interface InventoryService {

  PaginationDto<FullInventoryItemDto> getInventory(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, InventorySortByOption sortBy, boolean isAscending)
      throws BusinessNotFoundException, InsufficientPrivilegesException, UserNotFoundException, InventoryItemNotFoundException, InvalidAttributeValueException, InvalidPaginationInputException;


  long addInventoryItem(long businessId, CreateInventoryItemDto inventoryItem)
      throws InventoryRegistrationException, ProductNotFoundException, BusinessNotFoundException, UserNotFoundException, InsufficientPrivilegesException;


  InventoryItem getInventoryItemById(long businessId, long itemId)
      throws UserNotFoundException, InsufficientPrivilegesException, BusinessNotFoundException, InventoryItemNotFoundException;

  void updateInventoryItemFromPurchase(Long businessId, Listing listing)
      throws BusinessNotFoundException, InventoryItemNotFoundException, InventoryUpdateException;
    void deleteInventoryItem(InventoryItem inventoryItem);

    InventoryItem getInventoryItemById(Long inventoryItemId) throws InventoryItemNotFoundException;
}
