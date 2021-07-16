package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.MarketListingDao;
import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class MarketListingServiceImpl implements MarketListingService {

  private MarketListingDao marketListingDao;

  public MarketListingServiceImpl(@Autowired MarketListingDao marketListingDao) {
    this.marketListingDao = marketListingDao;
  }

  @Override
  @Transactional
  public Long saveMarketListing(MarketListing marketListing) {
    marketListing.setCreated(ZonedDateTime.now());
    marketListing.setDisplayPeriodEnd(ZonedDateTime.now().plus(1, ChronoUnit.MONTHS));
    this.marketListingDao.saveMarketListing(marketListing);
    return marketListing.getId();
  }

  @Override
  @Transactional
  public PaginationDto<FullMarketListingDto> getMarketListings(
      String section, String sortBy, Integer pagStartIndex, Integer pagEndIndex) {

    PaginationBuilder pagBuilder = new PaginationBuilder(MarketListing.class, "id");
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortByString(sortBy);

    Pair<List<MarketListing>, Long> dataAndTotalCount = marketListingDao.getMarketListing(section, pagBuilder);

    List<FullMarketListingDto> clientResults = new ArrayList<>();
    for (MarketListing marketListing : dataAndTotalCount.getFirst()) {
      clientResults.add(new FullMarketListingDto(marketListing));
    }
    return new PaginationDto<>(clientResults, dataAndTotalCount.getSecond());
  }
}
