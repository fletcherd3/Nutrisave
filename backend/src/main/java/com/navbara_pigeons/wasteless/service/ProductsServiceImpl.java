package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.ProductDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A ProductService Implementation
 */
@Service
public class ProductsServiceImpl  implements ProductsService{

    private final BusinessDao businessDao;

    private final ProductDao productDao;

    /**
     * ProductImplementation constructor that takes autowired parameters and sets up the
     * service for interacting with all business related services.
     *
     * @param businessDao The BusinessDataAccessObject.
     */
     @Autowired
     public ProductsServiceImpl(BusinessDao businessDao, ProductDao productDao) {
        this.businessDao = businessDao;
        this.productDao = productDao;
    }


    @Override
    public JSONObject getProducts(Business business) throws BusinessNotFoundException, UserNotFoundException {
        return null;
    }

    @Override
    public JSONObject addProduct(Product product, Business business) throws BusinessTypeException, ProductRegistrationException {
        return null;
    }
}
