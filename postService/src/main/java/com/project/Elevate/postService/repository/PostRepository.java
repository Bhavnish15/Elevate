package com.project.Elevate.postService.repository;

import com.project.Elevate.postService.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId, Pageable pageable);
}
