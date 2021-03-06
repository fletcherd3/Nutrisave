package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Keyword;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;

@Data
public class FullMarketListingDto {

  private Long id;
  private FullUserDto creator;
  private String section;
  private ZonedDateTime created;
  private ZonedDateTime displayPeriodEnd;
  private String title;
  private String description;
  private List<Keyword> keywords;

  public FullMarketListingDto(MarketListing marketListing) {
    this.id = marketListing.getId();
    this.creator = new FullUserDto(marketListing.getCreator());
    this.section = marketListing.getSection();
    this.created = marketListing.getCreated();
    this.displayPeriodEnd = marketListing.getDisplayPeriodEnd();
    this.title = marketListing.getTitle();
    this.description = marketListing.getDescription();
    this.keywords = marketListing.getKeywords();
  }

}
