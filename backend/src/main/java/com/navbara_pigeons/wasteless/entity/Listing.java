package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "LISTING")
public class Listing {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @ManyToOne(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinColumn(name = "INVENTORY_ITEM_ID")
  private InventoryItem inventoryItem;

  @Column(name = "QUANTITY")
  private long quantity;

  @Column(name = "PRICE")
  private float price;

  @Column(name = "MORE_INFO")
  private String moreInfo;

  @Column(name = "CREATED")
  private ZonedDateTime created;

  @Column(name = "CLOSES")
  private LocalDate closes;

  public Listing(CreateListingDto createListingDto) {
    this.id = createListingDto.getId();
    this.quantity = createListingDto.getQuantity();
    this.price = createListingDto.getPrice();
    this.moreInfo = createListingDto.getMoreInfo();
    this.closes = createListingDto.getCloses();
  }

  public Listing() {

  }
}
