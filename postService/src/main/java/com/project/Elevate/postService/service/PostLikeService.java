package com.project.Elevate.postService.service;

import com.project.Elevate.postService.auth.AuthContextHolder;
import com.project.Elevate.postService.entity.Post;
import com.project.Elevate.postService.entity.PostLike;
import com.project.Elevate.postService.event.PostLiked;
import com.project.Elevate.postService.exception.BadRequestException;
import com.project.Elevate.postService.exception.ResourceNotFoundException;
import com.project.Elevate.postService.repository.PostLikeRepository;
import com.project.Elevate.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<Long, PostLiked> postLikedKafkaTemplate;

    @Transactional
    public void likePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with Id: {} liking the post with ID: {}", userId, postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post could not found with this Id: {}"+ postId));

        boolean hasAlreadyLike = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(hasAlreadyLike) throw new BadRequestException("You can not like the post again.");

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        // TODO: send notification to the owner of the post.
        PostLiked postLiked = PostLiked.builder()
                .postId(postId)
                .ownerUserId(post.getUserId())
                .likedByUserId(userId)
                .build();
        postLikedKafkaTemplate.send("post_liked_topic", postLiked);

    }

    @Transactional
    public void unlikePost(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("User with Id: {} unliking the post with ID: {}", userId, postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post could not found with this Id: {}"+ postId));

        boolean hasAlreadyLike = postLikeRepository.existsByUserIdAndPostId(userId, postId);
        if(!hasAlreadyLike) throw new BadRequestException("You can not unlike the post again.");

        postLikeRepository.deleteByUserIdAndPostId(userId, postId);

    }
}









