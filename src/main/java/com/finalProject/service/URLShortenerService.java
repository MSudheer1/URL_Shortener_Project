package com.finalProject.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finalProject.entity.URLShortened;
import com.finalProject.repository.URLRepository;

@Service
public class URLShortenerService {

    @Autowired
    URLRepository urlRepository;

    // Shortens URL
    public URLShortened shortenUrl(String originalUrl, String expiryDate) {

        // Generate a short URL
    	String shortUrl = "https://short.ly/" + UUID.randomUUID().toString().substring(0, 6);


        // Parse expiry date
        LocalDate expDate = LocalDate.parse(expiryDate);

        // Ensure expiry date is not in the past
        if (expDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expiry date cannot be in the past.");
        }

        // Create a new URLShortened entity
        URLShortened url = new URLShortened();
        url.setShortUrl(shortUrl);
        url.setOriginalUrl(originalUrl);
        url.setExpiryDate(expDate);
        url.setCreatedAt(LocalDate.now());
        url.setClickCount(0);

        // Save and return the shortened URL
        return urlRepository.save(url);
    }

    // Redirects to original URL
    public String redirectUrl(String shortUrl) throws URLNotFoundException {
        // Retrieve the original URL associated with the short URL
        URLShortened url = urlRepository.findByShortUrl(shortUrl);

        if (url == null || url.getExpiryDate().isBefore(LocalDate.now())) {
            throw new URLNotFoundException("Short URL not found or expired: " + shortUrl);
        }
        

        // Increment click count (optional)
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    // URL analytics
	public URLShortened getAnalytics(String shortUrl) throws URLNotFoundException {
        URLShortened url = urlRepository.findByShortUrl(shortUrl); // Or your data fetch logic
        if (url == null) {
            throw new URLNotFoundException("Short URL not found");
        }
        return url;
    }

    // Updates the original URL
    public URLShortened updateUrl(String shortUrl, String newOriginalUrl) {
        URLShortened url = urlRepository.findById(shortUrl)
            .orElseThrow(() -> new URLNotFoundException("URL not found"));

        url.setOriginalUrl(newOriginalUrl);
        return urlRepository.save(url);
    }

    // Deletes a URL
    public void deleteUrl(String shortUrl) {
        urlRepository.deleteById(shortUrl);
    }

    // Custom exception for URL not found
    public static class URLNotFoundException extends RuntimeException {
        public URLNotFoundException(String message) {
            super(message);
            
        }
    }
}
