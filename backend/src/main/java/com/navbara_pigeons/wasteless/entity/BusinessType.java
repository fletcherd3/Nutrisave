package com.navbara_pigeons.wasteless.entity;

public enum BusinessType {
  ACCOMMODATION_AND_FOOD("Accommodation and Food Services"),
  RETAIL("Retail Trade"),
  CHARITY("Charitable organisation"),
  NON_PROFIT("Non-profit organisation");

  String value;
  BusinessType(String value) {
       this.value = value;
  }

  /**
   * Returns string value of the business type
   */
  public String toString() {
       return this.value;
  }

  /**
   * Converts string to BusinessType (using the enum value, not the name of the enum)
   * @param businessType string to convert
   * @return enum value
   * @throws IllegalArgumentException if invalid string given
   */
  public static BusinessType fromString(String businessType) throws IllegalArgumentException {
    for (BusinessType b : BusinessType.values()) {
      if (b.value.equals(businessType)) {
        return b;
      }
    }

    throw new IllegalArgumentException(String.format("Invalid business type given; got '%s'", businessType));
  }
}