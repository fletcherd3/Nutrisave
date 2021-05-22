package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;

import java.time.LocalDate;

public class InventoryServiceValidation {

  /**
   * Checks if the required fields are not empty
   *
   * @param inventory item to validate
   * @return true if the product is valid
   */
  public static void requiredFieldsNotEmpty(Inventory inventory) throws InventoryRegistrationException {
    if (inventory.getExpires() == null) {
      throw new InventoryRegistrationException("Expiry date is empty");
    } else if ( inventory.getTotalPrice() == null ) {
      throw new InventoryRegistrationException("Total price is empty");
    } else if ( inventory.getPricePerItem() == null ) {
      throw new InventoryRegistrationException("Price per item is empty");
    }
  }

  /**
   * Checks if a date is after another. for checking the expiration is after current date.
   *
   * @param inventory item to validate
   * @return true if the product is valid
   */
  protected static boolean date1AfterDate2(LocalDate date1, LocalDate date2) {
    if (date1 == null || date2 == null) return false;
    return date1.isAfter(date2);
  }

  /**
   * Checks if date orders are valid, current date before expiry etc. passing
   *
   * @param inventory
   */
  public static void datesValid(Inventory inventory, LocalDate  currentDate) throws InventoryRegistrationException {
    // TODO how do we ensure local date is equal to date of the user?
    if (InventoryServiceValidation.date1AfterDate2(inventory.getManufactured(), currentDate)) {
      throw new InventoryRegistrationException("Manufacture date must be before or equal to today");
    }
    if (InventoryServiceValidation.date1AfterDate2(inventory.getManufactured(), inventory.getBestBefore())) {
      throw new InventoryRegistrationException("Best before date must be after manufacture date");
    }
    if (InventoryServiceValidation.date1AfterDate2(inventory.getBestBefore(), inventory.getSellBy())) {
      throw new InventoryRegistrationException("Sell by date must be after best before date");
    }
    if (InventoryServiceValidation.date1AfterDate2(currentDate, inventory.getExpires())) {
      throw new InventoryRegistrationException("Expiry date must be on or after today");
    }
  }

  /**
   * Checks if price is valid: positive and less than 10000
   *
   * @param price price to check
   * @return if price is valid or null
   */
  public static void priceValid(Double price) throws InventoryRegistrationException {
    if (price == null) return;
    if (price < 0) {
      throw new InventoryRegistrationException("Price cannot be negative");
      // test gets broken when user inputs 9999.999
    } else if (price > 9999.99) {
      throw new InventoryRegistrationException("Price cannot be more than 10000");
    }
  }

  /**
   * Checks if quantity is valid: positive
   *
   * @param quantity quantity to check
   */
  public static void quantityValid(long quantity) throws InventoryRegistrationException {
    if (quantity < 0) {
      throw new InventoryRegistrationException("quantity cannot be negative");
    }

  }
  /**
   * performs all checks on Inventory item
   *
   * @param inventory user input and calculated fields to check
   */
  public static void isInventoryItemValid(Inventory inventory) throws InventoryRegistrationException {
    LocalDate currentDate = LocalDate.now();
    InventoryServiceValidation.requiredFieldsNotEmpty(inventory);
    InventoryServiceValidation.datesValid(inventory, currentDate);
    InventoryServiceValidation.priceValid(inventory.getPricePerItem());
    InventoryServiceValidation.quantityValid(inventory.getQuantity());
  }
}