package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import java.time.LocalDate;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class FullListingDto {
  private long id;
  private FullInventoryItemDto inventoryItem;
  private long quantity;
  private Double price;
  private String moreInfo;
  private ZonedDateTime created;
  private ZonedDateTime closes;

  public FullListingDto(Listing listing) {
    id = listing.getId();
    inventoryItem = new FullInventoryItemDto(listing.getInventoryItem());
    quantity = listing.getQuantity();
    price = listing.getPrice();
    moreInfo = listing.getMoreInfo();
    created = listing.getCreated();
    closes = listing.getCloses();
    moreInfo = listing.getMoreInfo();
  }

  public FullListingDto() {

  }
}