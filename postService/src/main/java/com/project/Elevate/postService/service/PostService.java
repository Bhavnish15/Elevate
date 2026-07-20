package com.project.Elevate.postService.service;


import com.project.Elevate.postService.auth.AuthContextHolder;
import com.project.Elevate.postService.client.ConnectionsServiceClient;
import com.project.Elevate.postService.client.UploaderServiceClient;
import com.project.Elevate.postService.dto.PersonDto;
import com.project.Elevate.postService.dto.PostCreateRequestDto;
import com.project.Elevate.postService.dto.PostDto;
import com.project.Elevate.postService.entity.Post;
import com.project.Elevate.postService.event.PostCreated;
import com.project.Elevate.postService.exception.ResourceNotFoundException;
import com.project.Elevate.postService.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsServiceClient connectionsServiceClient;
    private final KafkaTemplate<Long, PostCreated> postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;

    public PostDto createPost(PostCreateRequestDto postCreateRequestDto, MultipartFile file) {
        Long userId = AuthContextHolder.getCurrentUserId();
        log.info("Creating new post with userID: {}", userId);

        ResponseEntity<String> imageUrl = uploaderServiceClient.uploadFile(file);


        Post post = modelMapper.map(postCreateRequestDto, Post.class);
        post.setUserId(userId);
        post.setImageUrl(imageUrl.getBody());
        post = postRepository.save(post);
        List<PersonDto> personDtoList = connectionsServiceClient.getFirstDegreeConnections(userId);


        for(PersonDto person: personDtoList){  // Send Notification
            PostCreated postCreated = PostCreated.builder()
                    .postId(post.getId())
                    .content(post.getContent())
                    .userId(person.getUserId())
                    .ownerUserId(userId)
                    .build();
            postCreatedKafkaTemplate.send("post_created_topics", postCreated);
        }

        log.info("The post has been Created.");
        return modelMapper.map(post, PostDto.class);

    }

    public PostDto getPostById(Long postId) {
//        Long userId = AuthContextHolder.getCurrentUserId();


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
