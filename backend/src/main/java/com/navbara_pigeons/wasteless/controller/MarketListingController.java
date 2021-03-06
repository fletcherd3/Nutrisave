package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateMarketListingDto;
import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.enums.MarketListingSortByOption;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidMarketListingSectionException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.MarketListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("")
@Tag(name = "Marketplace Endpoint (Cards)", description = "The API endpoint for a virtual marketplace")
public class MarketListingController {

  private final MarketListingService marketListingService;

  @Autowired
  public MarketListingController(MarketListingService marketListingService) {
    this.marketListingService = marketListingService;
  }

  /**
   * Create a market listing card
   *
   * @param createMarketListingDto
   * @return Response entity with cardId if successful.
   * @throws UserNotFoundException
   * @throws InsufficientPrivilegesException
   */
  @PostMapping("/cards")
  public ResponseEntity<JSONObject> addMarketListing(
      @Valid @RequestBody CreateMarketListingDto createMarketListingDto)
      throws UserNotFoundException, InsufficientPrivilegesException {
    log.info("CREATING A CARD WITH TITLE: " + createMarketListingDto.getTitle());

    JSONObject response = new JSONObject();
    response.put("cardId", marketListingService.saveMarketListing(
        new MarketListing(createMarketListingDto),
        createMarketListingDto.getKeywordIds(),
        createMarketListingDto.getCreatorId()
    ));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * @param section       The section of the marketplace
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null and defaults to the 'id'
   *                      field.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return List of all paginated/sorted market listings that match the section String
   */
  @GetMapping("/cards")
  @Operation(summary = "Show marketplace cards", description = "Return a paginated/sorted list of marketplace cards for a section")
  public ResponseEntity<PaginationDto<FullMarketListingDto>> getMarketListings(
      @Parameter(
          description = "The section for which cards should be retrieved."
      ) @RequestParam String section,
      @Parameter(
          description = "The start index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagStartIndex,
      @Parameter(
          description = "The stop index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagEndIndex,
      @Parameter(
          description = "Defines the field to be sorted, can be null."
      ) @RequestParam(required = false) MarketListingSortByOption sortBy,
      @Parameter(
          description = "Boolean value, whether the sort order should be in ascending order. Is not"
              + " required and defaults to True."
      ) @RequestParam(required = false, defaultValue = "true") boolean isAscending)
      throws InvalidPaginationInputException, InvalidMarketListingSectionException {
    log.info("GETTING CARDS FROM THE '" + section + "' SECTION");
    return new ResponseEntity<>(
        this.marketListingService
            .getMarketListings(section, sortBy, pagStartIndex, pagEndIndex, isAscending),
        HttpStatus.OK);
  }
}
