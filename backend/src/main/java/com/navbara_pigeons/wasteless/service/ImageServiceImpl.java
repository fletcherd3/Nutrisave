package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ImageDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ImageNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {

  private final UserDao userDao;
  private final ImageDao imageDao;
  private final BusinessService businessService;
  private final ProductService productService;
  private final UserService userService;
  private final int THUMBNAIL_DIMENSIONS = 300;
  @Value("${image.products.prefix}")
  private String imagePrefix;

  @Autowired
  public ImageServiceImpl(UserDao userDao, ImageDao imageDao, BusinessService businessService,
      ProductService productService, UserService userService) {
    this.userDao = userDao;
    this.imageDao = imageDao;
    this.businessService = businessService;
    this.productService = productService;
    this.userService = userService;
  }

  /**
   * Upload an image to a businesses product
   *
   * @param businessId The identifier of a business
   * @param productId  The identifier of a product to add the image to
   * @param image      The image to be uploaded
   * @throws UserNotFoundException     The users credentials could not be found from the JSessionID
   * @throws BusinessNotFoundException When no business is found with the given id
   * @throws ProductNotFoundException  When no product is found with the given id
   * @throws IOException               When an IO error occurs (will return a 500 status error, this
   *                                   is intended)
   * @throws ImageNotFoundException    When a non image file or no file is received instead of an
   *                                   image
   */
  @Transactional
  @Override
  public void uploadProductImage(long businessId, long productId, MultipartFile image)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, IOException,
      ImageNotFoundException {
    if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
      throw new BadCredentialsException(
          "You must be an administrator of the business or a GAA to upload a product image");
    }

    // Get the file extension of the given file
    String fileName = StringUtils.cleanPath(image.getOriginalFilename());
    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

    // Check if "image" is actually an image
    ArrayList<String> items = new ArrayList<>();
    items.add("jpg");
    items.add("jpeg");
    items.add("png");
    items.add("gif");
    if (!items.contains(fileExtension.toLowerCase())) {
      throw new ImageNotFoundException();
    }

    // Crop the image and then save it to the DB + Machine
    MultipartFile imageThumbnail = cropImageToSquare(image, fileExtension, fileName);
    imageThumbnail = createImageThumbnail(imageThumbnail, fileExtension);
    Image imageEntity = new Image(imagePrefix, fileExtension);
    Product productEntity = productService.getProduct(productId);
    productEntity.addProductImage(imageEntity);

    imageDao.saveProductImageToMachine(image, imageEntity.getPath());
    imageDao.saveProductImageToMachine(imageThumbnail, imageEntity.getThumbnailPath());
    imageDao.saveProductImageToDb(imageEntity);

    if (productEntity.getPrimaryProductImage() == null) {
      productEntity.setPrimaryProductImage(imageEntity);
    }
  }

  /**
   * Change primary image
   *
   * @param businessId The identifier of a business
   * @param productId  The identifier of a product to add the image to
   * @param imageId    The identifier of an image to be set as the primary image
   */
  public void changePrimaryImage(long businessId, long productId, long imageId)
      throws UserNotFoundException, BusinessNotFoundException, ProductNotFoundException, ImageNotFoundException {
    if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
      throw new BadCredentialsException(
          "You must be an administrator of the business or a GAA to change the primary image");
    }

    Product productEntity = productService.getProduct(productId);
    Image newPrimaryImage = productEntity.getImageById(imageId);
    productEntity.setPrimaryProductImage(newPrimaryImage);
    this.productService.saveProduct(productEntity);
  }

  private MultipartFile createImageThumbnail(MultipartFile image, String extension)
      throws IOException {
    InputStream in = new ByteArrayInputStream(image.getBytes());
    BufferedImage imageToResize = ImageIO.read(in);

    int targetWidth = THUMBNAIL_DIMENSIONS;
    int targetHeight = targetWidth;

    java.awt.Image resultingImage = imageToResize.getScaledInstance(targetWidth, targetHeight,
        java.awt.Image.SCALE_DEFAULT);
    BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight,
        BufferedImage.TYPE_INT_RGB);
    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(outputImage, extension, baos);

    return new MockMultipartFile("image", baos.toByteArray());
  }

  private MultipartFile cropImageToSquare(MultipartFile image, String extension, String fileName)
      throws IOException {
    InputStream in = new ByteArrayInputStream(image.getBytes());
    BufferedImage imageToCrop = ImageIO.read(in);

    // Get the image dimensions
    int height = imageToCrop.getHeight();
    int width = imageToCrop.getWidth();

    // The image is not already a square
    if (height != width) {
      // Compute the size of the square
      int squareSize = (Math.min(height, width));

      // Coordinates of the image's middle
      int xc = width / 2;
      int yc = height / 2;

      // Crop the image
      imageToCrop = imageToCrop.getSubimage(
          xc - (squareSize / 2), // x coordinate of the upper-left corner
          yc - (squareSize / 2), // y coordinate of the upper-left corner
          squareSize,               // width
          squareSize                // height
      );
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(imageToCrop, extension, baos);
    return new MockMultipartFile(fileName, baos.toByteArray());
  }

  /**
   * This service method deletes the product image associated with the business/product if the user
   * has the correct permissions.
   *
   * @param imageId    The ID of the image that is to be deleted.
   * @param businessId The ID of the business whose product image is being deleted. (Used to check
   *                   for user permissions)
   * @param productId  The ID of the product whose image is being deleted.
   * @throws UserNotFoundException           Thrown if no user is logged in.
   * @throws BusinessNotFoundException       Thrown if the business that owns the product does not
   *                                         exist.
   * @throws InsufficientPrivilegesException Thrown if the user is not admin or business admin
   * @throws ProductNotFoundException        Thrown if the product in question does not exist.
   * @throws ImageNotFoundException          Thrown if the image in question does not exist.
   * @throws IOException                     Thrown if the system is unable to delete the actual
   *                                         file from persistent storage.
   */
  @Transactional
  public void deleteProductImage(long imageId, long businessId, long productId)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException, ProductNotFoundException, ImageNotFoundException, IOException {
    if (!businessService.isBusinessAdmin(businessId) && !userService.isAdmin()) {
      throw new BadCredentialsException(
          "You must be an administrator of the business or a GAA to delete this image");
    }

    Product product = this.productService.getProduct(productId);
    Image image = product.getImageById(imageId);
    product.deleteProductImage(imageId);
    this.productService.saveProduct(product);
    this.imageDao.deleteImage(image);
    this.imageDao.deleteProductImageFromMachine(image.getPath());
    this.imageDao.deleteProductImageFromMachine(image.getThumbnailPath());
  }
}
