package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller class contains endpoints that deal with keywords.
 */
@RestController
public class KeywordController {

    private final KeywordService keywordService;

    public KeywordController(@Autowired KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    /**
     * This endpoint returns a list of all keywords.
     * @return ResponseEntity
     */
    @GetMapping("/keywords")
    public ResponseEntity getAllKeywords() {
        return new ResponseEntity(keywordService.getAllKeywords(), HttpStatus.valueOf(200));
    }

}