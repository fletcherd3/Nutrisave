package com.navbara_pigeons.wasteless.entity;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "INVENTORY")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @OneToOne()
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @OneToOne()
    @JoinColumn(name = "BUSINESS_ID")
    private Business business;

    @Column(name = "QUANTITY")
    private long quantity;

    @Column(name = "PRICE")
    private float price;

    @Column(name = "TOTAL_PRICE")
    private float total_price;

    @Column(name = "EXPIRES")
    private ZonedDateTime expires;

    @Column(name = "MANUFACTERED")
    private ZonedDateTime manufacured;

    @Column(name = "SELL_BY")
    private ZonedDateTime sell_by;

    @Column(name = "BEST_BEFORE")
    private ZonedDateTime best_before;

}
