package com.finalProject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class URLShortened {

	@Id
	@Column(name = "short_url")
	private String shortUrl;

	@Column(name = "original_url", nullable = false)
	private String originalUrl;

	@Column(name = "expiry_date", nullable = false)
	private LocalDate expiryDate;

	@Column(name = "created_at", nullable = false)
	private LocalDate createdAt;

	@Column(name = "click_count", nullable = false)
	private int clickCount;

	// Constructors, getters, and setters

	public URLShortened() {
	}

	public URLShortened(String shortUrl, String originalUrl, LocalDate expiryDate, LocalDate createdAt,
			int clickCount) {
		this.shortUrl = shortUrl;
		this.originalUrl = originalUrl;
		this.expiryDate = expiryDate;
		this.createdAt = createdAt;
		this.clickCount = clickCount;
	}

	// Getter and Setter methods
	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public int getClickCount() {
		return clickCount;
	}

	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
}
