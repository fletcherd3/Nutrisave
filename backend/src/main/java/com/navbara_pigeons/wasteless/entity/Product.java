package com.navbara_pigeons.wasteless.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.ZonedDateTime;
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
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navbara_pigeons.wasteless.exception.ImageNotFoundException;
import lombok.Data;

@Data
@Entity
@Table(name = "PRODUCT")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private long id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "DESCRIPTION")
  private String description;

  @Column(name = "CURRENCY")
  private String currency;
  
  @Column(name = "MANUFACTURER")
  private String manufacturer;

  @Column(name = "RRP")
  private Double recommendedRetailPrice;

  @Column(name = "CREATED")
  private ZonedDateTime created;

  @JsonIgnore
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "PRIMARY_IMAGE_ID", referencedColumnName = "ID")
  private Image primaryProductImage;

  @JsonIgnore
  @OneToMany(
      fetch = FetchType.LAZY,
      cascade = {
          CascadeType.DETACH,
          CascadeType.MERGE,
          CascadeType.PERSIST,
          CascadeType.REFRESH
      }
  )
  @JoinTable(
      name = "PRODUCT_IMAGE",
      joinColumns = @JoinColumn(name = "PRODUCT_ID"),
      inverseJoinColumns = @JoinColumn(name = "IMAGE_ID")
  )
  private List<Image> productImages;

  /**
   * This is a helper method for adding a image to the product.
   *
   * @param image The product to be added.
   */
  public void addProductImage (Image image) {
    if (this.productImages == null) {
      this.productImages = new ArrayList<>();
    }
    this.productImages.add(image);
  }

  public void deleteProductImage(long id) throws ImageNotFoundException {
    Image imageToRemove = getImageById(id);
    if (this.primaryProductImage == imageToRemove && this.productImages.size() > 1) {
      this.productImages.remove(imageToRemove);
      this.primaryProductImage = this.productImages.get(0);
    } else if (this.primaryProductImage == imageToRemove && this.productImages.size() == 1) {
      this.productImages.remove(imageToRemove);
      this.primaryProductImage = null;
    } else {
      this.productImages.remove(imageToRemove);
    }
  }

  public Image getImageById(long id) throws ImageNotFoundException {
    for (Image image : this.productImages) {
      if (image.getId() == id) {
        return image;
      }
    }
    throw new ImageNotFoundException("The image can't be found");
  }

  /**
   * get method for productImages used by json.
   * @return the list of product images with the primary image as the first item of the list
   */
  public List<Image> getImages() {
    List<Image> images = this.productImages;
    Image primaryImage = this.getPrimaryProductImage();
    if (primaryImage != null) {
      images.remove(primaryImage);
      images.add(0, primaryImage);
    }
    return images;
  }
}