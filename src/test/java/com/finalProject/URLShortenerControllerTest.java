package com.finalProject;

import com.finalProject.controller.URLShortenerController;
import com.finalProject.entity.URLShortened;
import com.finalProject.service.URLShortenerService;
import com.finalProject.service.URLShortenerService.URLNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class URLShortenerControllerTest {

	private MockMvc mockMvc;

	@Mock
	URLShortenerService urlShortenerService;

	@InjectMocks
	private URLShortenerController urlShortenerController;

	@Test
	void testShortenUrl_Success() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		URLShortened mockUrl = new URLShortened();
		mockUrl.setShortUrl("short123");
		mockUrl.setOriginalUrl("https://example.com");
		mockUrl.setExpiryDate(LocalDate.now().plusDays(10));

		when(urlShortenerService.shortenUrl(anyString(), anyString())).thenReturn(mockUrl);

		mockMvc.perform(post("/shorten").contentType(MediaType.APPLICATION_JSON)
				.content("{\"originalUrl\":\"https://example.com\",\"expiryDate\":\"2025-12-31\"}"))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.shortUrl").value("short123"))
				.andExpect(jsonPath("$.originalUrl").value("https://example.com"));
	}

	@Test
	void testShortenUrl_BadRequest() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		mockMvc.perform(post("/shorten").contentType(MediaType.APPLICATION_JSON).content("{}")) // Missing required
																								// parameters
				.andExpect(status().isBadRequest());
	}

	@Test
	void testRedirectUrl_Success() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		when(urlShortenerService.redirectUrl("short123")).thenReturn("https://example.com");

		mockMvc.perform(get("/redirect").contentType(MediaType.APPLICATION_JSON).content("{\"shortUrl\":\"short123\"}"))
				.andExpect(status().isOk()).andExpect(content().string("https://example.com"));
	}

	@Test
	void testRedirectUrl_NotFound() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		when(urlShortenerService.redirectUrl(anyString())).thenThrow(new URLNotFoundException("Not found"));

		mockMvc.perform(get("/redirect").contentType(MediaType.APPLICATION_JSON).content("{\"shortUrl\":\"invalid\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void testGetAnalytics_Success() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		URLShortened mockUrl = new URLShortened();
		mockUrl.setShortUrl("https://short.ly/short123");
		mockUrl.setOriginalUrl("https://example.com");
		mockUrl.setExpiryDate(LocalDate.now().plusDays(10));

		when(urlShortenerService.getAnalytics("https://short.ly/short123")).thenReturn(mockUrl);

		mockMvc.perform(get("/analytics/short123")).andExpect(status().isOk());
		MvcResult result = mockMvc.perform(get("/analytics/short123")).andExpect(status().isOk()).andReturn();

		System.out.println("Response: " + result.getResponse().getContentAsString());

	}

	@Test
	void testGetAnalytics_NotFound() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		when(urlShortenerService.getAnalytics(anyString())).thenThrow(new URLNotFoundException("Not found"));

		mockMvc.perform(get("/analytics/invalid")).andExpect(status().isNotFound());
	}

	@Test
	void testUpdateUrl_Success() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		URLShortened mockUrl = new URLShortened();
		mockUrl.setShortUrl("short123");
		mockUrl.setOriginalUrl("https://new-example.com");

		when(urlShortenerService.updateUrl(anyString(), anyString())).thenReturn(mockUrl);

		mockMvc.perform(put("/update/{shortUrl}", "short123").contentType(MediaType.APPLICATION_JSON)
				.content("{\"newOriginalUrl\":\"https://new-example.com\"}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.originalUrl").value("https://new-example.com"));
	}

	@Test
	void testUpdateUrl_NotFound() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

		when(urlShortenerService.updateUrl(anyString(), anyString())).thenThrow(new URLNotFoundException("Not found"));

		mockMvc.perform(put("/update/{shortUrl}", "invalid").contentType(MediaType.APPLICATION_JSON)
				.content("{\"newOriginalUrl\":\"https://new-example.com\"}")).andExpect(status().isNotFound());
	}

	@Test
	void testDeleteUrl_Success() throws Exception {
		System.out.println("In delete success test case");
		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();
		URLShortened mockUrl = new URLShortened(); // Create a fake object
		when(urlShortenerService.getAnalytics("https://short.ly/short123")).thenReturn(mockUrl);

		doNothing().when(urlShortenerService).deleteUrl("https://short.ly/short123");

		mockMvc.perform(delete("/delete/{shortUrl}", "short123")).andExpect(status().isOk());
	}

	@Test
	void testDeleteUrl_NotFound() throws Exception {

		mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();
		when(urlShortenerService.getAnalytics("https://short.ly/invalid")).thenReturn(null);

		mockMvc.perform(delete("/delete/{shortUrl}", "invalid")).andExpect(status().isNotFound());
	}
}
