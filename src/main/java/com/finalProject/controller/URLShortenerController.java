package com.finalProject.controller;

import com.finalProject.entity.URLShortened;
import com.finalProject.service.URLShortenerService;
import com.finalProject.service.URLShortenerService.URLNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class URLShortenerController {

    @Autowired
    private URLShortenerService urlShortenerService;

    @PostMapping("/shorten")
    public ResponseEntity<URLShortened> shortenUrl(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("originalUrl");
        String expiryDate = request.get("expiryDate");

        if (originalUrl == null || expiryDate == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            URLShortened shortenedUrl = urlShortenerService.shortenUrl(originalUrl, expiryDate);
            return new ResponseEntity<>(shortenedUrl, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/redirect", produces = "application/json")
    public ResponseEntity<String> redirectUrl(@RequestBody URLShortened shortUrl) {
        try {
            System.out.println("Received redirect request for: " + shortUrl.getShortUrl());
            String originalUrl = urlShortenerService.redirectUrl(shortUrl.getShortUrl());
            System.out.println("Redirecting to: " + originalUrl);
            return ResponseEntity.status(HttpStatus.OK)  // 200 OK status
                    .body(originalUrl); 
        } catch (URLNotFoundException e) {
            System.out.println("Short URL not found: " + shortUrl);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/analytics/{shortUrl}")
    public ResponseEntity<URLShortened> getAnalytics(@PathVariable String shortUrl) {
        try {
        	shortUrl = "https://short.ly/"+shortUrl;
            System.out.println("Received redirect request for: " + shortUrl);
            URLShortened url = urlShortenerService.getAnalytics(shortUrl);
            System.out.println("Long URL " + url.getOriginalUrl());
            return new ResponseEntity<>(url, HttpStatus.OK);
        } catch (URLNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{shortUrl}")
    public ResponseEntity<URLShortened> updateUrl(@PathVariable String shortUrl, @RequestBody Map<String, String> request) {
        String newOriginalUrl = request.get("newOriginalUrl");
        System.out.println("New Original URL: " + newOriginalUrl);

        if (newOriginalUrl == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            URLShortened updatedUrl = urlShortenerService.updateUrl("https://short.ly/"+shortUrl, newOriginalUrl);
            return new ResponseEntity<>(updatedUrl, HttpStatus.OK);
        } catch (URLNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{shortUrl}")
    public ResponseEntity<Map<String, String>> deleteUrl(@PathVariable String shortUrl) {
    	String shortUrlPath = "https://short.ly/"+shortUrl;
    	
    	try {
			URLShortened url = urlShortenerService.getAnalytics(shortUrlPath);
            if (url == null) {
                System.out.println("Short URL not found: " + shortUrlPath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "URL not found"));
            }

            urlShortenerService.deleteUrl(shortUrlPath);
            System.out.println("✅ Deleted successfully: " + shortUrl);
            return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }

}
