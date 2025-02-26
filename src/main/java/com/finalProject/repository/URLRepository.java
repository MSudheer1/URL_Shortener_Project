package com.finalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.finalProject.entity.URLShortened;

@Repository
public interface URLRepository extends JpaRepository<URLShortened, String> {

	URLShortened findByShortUrl(String shortUrl);

}
