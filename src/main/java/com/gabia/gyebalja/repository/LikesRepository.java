package com.gabia.gyebalja.repository;

import com.gabia.gyebalja.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
}