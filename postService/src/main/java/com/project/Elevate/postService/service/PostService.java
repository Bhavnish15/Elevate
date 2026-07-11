package com.project.Elevate.postService.service;


import com.project.Elevate.postService.auth.AuthContextHolder;
import com.project.Elevate.postService.client.ConnectionsServiceClient;
import com.project.Elevate.postService.dto.PersonDto;
import com.project.Elevate.postService.dto.PostCreateRequestDto;
import com.project.Elevate.postService.dto.PostDto;
import com.project.Elevate.postService.entity.Post;
import com.project.Elevate.postService.exception.ResourceNotFoundException;
import com.project.Elevate.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId) {
        log.info("Creating new post with userID: {}", userId);
        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        post = postRepository.save(post);
        log.info("The post has been Created.");
        return modelMapper.map(post, PostDto.class);

    }

    public PostDto getPostById(Long postId) {
        Long userId = AuthContextHolder.getCurrentUserId();
        // TODO: REMOVE in future
        // Call the Connection service fro posts service and pass the userId inside the headers
        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId);
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post Not Found "+ "with ID: "+postId));
        return modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostOfUser(Long userId, Pageable pageable) {
        List<Post> posts =  postRepository.findByUserId(userId, pageable);
        return posts.stream().map((el) -> modelMapper.map(el, PostDto.class))
                .collect(Collectors.toList());
    }

    public Page<Post> getAllPost(Pageable pageable) {
        return postRepository.findAll(pageable);
    }
}
