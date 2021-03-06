Feature: U22 List sale

#  AC2: I can add a listing using an appropriate sequence of UI actions. This includes selecting
#  an inventory entry: the application will then transfer the appropriate fields to the sale listing.
#  These include the number of items for sale, which must be greater than zero and less than or
#  equal to the number in the corresponding inventory entry. The name and thumbnail image
#  of the corresponding product should also be included. Note: If a sale listing does not include
#  all of the items in an inventory entry then the remaining items may be included in other
#  sale listings (perhaps with dif erent closing dates and prices). However, we cannot list for
#  sale more items than are in the corresponding inventory entry. For example, if I have 50
#  punnets of strawberries due to expire on Saturday, I could of er 30 of them in one sale
#  listing and the remaining 20 in another — but I couldn’t have two listings of 30.
#
#  AC3: If the number of items in the listing is less than that in the corresponding inventory
#  entry, and the inventory contains a non-null item price, then that is used to calculate the
#  total price for the listing.
#
#  AC4: The price for the listing may be overridden. An optional field for further information
#  (e.g. “seller may be willing to consider near offers”) is available.

  Scenario: AC2 - Quantity must be above 0 and less than quantity of inventory entry - other listings of that item quantity
    Given a user has a business "Jovial Jerky" in "New Zealand"
    And the business has the product "Jenkin's Jerky" with RRP of 7.99
    And my business has 40 of them in stock at 8.99
    And a listing with quantity 20 and price 8.99 exists
    When i create another listing with quantity 21 and price 150.00
    Then appropriate error messages are shown

  Scenario: AC4 - Listing has optional field: moreInfo
    Given a user has a business "Jovial Jerky" in "New Zealand"
    And the business has the product "Jenkin's Jerky" with RRP of 7.99
    And my business has 40 of them in stock at 8.99
    When i list 20 of these for sale mentioning "ONO" as more info on the listing
    Then i can see the listing is created with this extra field "ONO"

  Scenario: AC5 - Closing uses inventory entry expiry date as default when not supplied
    Given a user has a business "Jovial Jerky" in "New Zealand"
    And the business has the product "Jenkin's Jerky" with RRP of 7.99
    And my business has 40 of them in stock at 8.99
    When i list 20 of these for sale with no closing date supplied
    Then i can see the listing is created with the expiry date as the closing date
