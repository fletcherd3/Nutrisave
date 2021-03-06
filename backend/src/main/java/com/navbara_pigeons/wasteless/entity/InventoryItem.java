package com.navbara_pigeons.wasteless.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.FullInventoryItemDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
@Entity
@Table(name = "INVENTORY_ITEM")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @OneToOne()
  @JoinColumn(name = "PRODUCT_ID")
  private Product product;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "BUSINESS_ID")
  private Business business;

  @Column(name = "QUANTITY")
  private long quantity;

  @DecimalMin(message = "pricePerItem must be above 0.01", value = "0.01")
  @DecimalMax(message = "pricePerItem must be below 10,000,000", value = "10000000.00")
  @Column(name = "PRICE_PER_ITEM")
  private Double pricePerItem;

  @DecimalMin(message = "totalPrice must be above 0.01", value = "0.01")
  @DecimalMax(message = "totalPrice must be below 10,000,000", value = "10000000.00")
  @Column(name = "TOTAL_PRICE")
  private Double totalPrice;

  @Column(name = "EXPIRES")
  private LocalDate expires;

  @Column(name = "MANUFACTURED")
  private LocalDate manufactured;

  @Column(name = "SELL_BY")
  private LocalDate sellBy;

  @Column(name = "BEST_BEFORE")
  private LocalDate bestBefore;

  @JsonIgnore
  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      },
      orphanRemoval = true,
      mappedBy = "inventoryItem"
  )
  private List<Listing> listings;

  public InventoryItem(FullInventoryItemDto inventory) {
    this.id = inventory.getId();
    this.product = new Product(inventory.getProduct());
    this.quantity = inventory.getQuantity();
    this.pricePerItem = inventory.getPricePerItem();
    this.totalPrice = inventory.getTotalPrice();
    this.expires = inventory.getExpires();
    this.manufactured = inventory.getManufactured();
    this.sellBy = inventory.getSellBy();
    this.bestBefore = inventory.getBestBefore();
    this.business = new Business(inventory.getBusiness());
  }

  public InventoryItem(CreateInventoryItemDto inventory) {
    this.quantity = inventory.getQuantity();
    this.pricePerItem = inventory.getPricePerItem();
    this.totalPrice = inventory.getTotalPrice();
    this.expires = inventory.getExpires();
    this.manufactured = inventory.getManufactured();
    this.sellBy = inventory.getSellBy();
    this.bestBefore = inventory.getBestBefore();
  }

  public InventoryItem() {
  }

  /**
   * Helper method which adds a listing to a inventory item
   *
   * @param listing
   */
  public void addListing(Listing listing) {
    if (listings == null) {
      listings = new ArrayList<>();
    }
    listings.add(listing);
  }

  /**
   * Updates the inventory items quantity by removing a given value
   *
   * @param quantity to remove by
   */
  public void removeQuantity(long quantity) {
    this.setQuantity(this.quantity - quantity);
  }
}
