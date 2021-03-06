package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class FullInventoryItemDto {

  private long id;
  private BasicProductDto product;
  private long quantity;
  private Double pricePerItem;
  private Double totalPrice;
  private LocalDate expires;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;
  private BasicBusinessDto business;

  public FullInventoryItemDto(InventoryItem inventoryItem, String publicPathPrefix) {
    id = inventoryItem.getId();
    product = new BasicProductDto(inventoryItem.getProduct(), publicPathPrefix);
    quantity = inventoryItem.getQuantity();
    pricePerItem = inventoryItem.getPricePerItem();
    totalPrice = inventoryItem.getTotalPrice();
    expires = inventoryItem.getExpires();
    manufactured = inventoryItem.getManufactured();
    sellBy = inventoryItem.getSellBy();
    bestBefore = inventoryItem.getBestBefore();
    if (inventoryItem.getBusiness() == null) {
      business = null;
    } else {
      business = new BasicBusinessDto(inventoryItem.getBusiness());
    }
  }

  public FullInventoryItemDto() {

  }

}
