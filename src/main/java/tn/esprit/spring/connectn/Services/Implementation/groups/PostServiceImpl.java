package tn.esprit.spring.connectn.Services.Implementation.groups;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.FileType;
import tn.esprit.spring.connectn.Entities.groups.Group;
import tn.esprit.spring.connectn.Entities.groups.Post;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Repository.groups.GroupsRepository;
import tn.esprit.spring.connectn.Repository.groups.PostRepository;
import tn.esprit.spring.connectn.Services.Interfaces.groups.IPostService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostServiceImpl implements IPostService {

    private final PostRepository postRepository;
    private final GroupsRepository groupsRepository;
    // private final UserRepository userRepository;
    @Autowired
    private UserRepository userRepository;

    @Override

    public Post createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setLikeCount(0);
        post.setIsLikedByCurrentUser(false);
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Post updatePost(Long postId, Post postDetails) {
        Post post = getPostById(postId);
        post.setContent(postDetails.getContent());
        post.setMediaUrl(postDetails.getMediaUrl());
        post.setMediaType(postDetails.getMediaType());
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public List<Post> getPostsByGroup(Long groupId) {
        return postRepository.findByGroup_GroupId(groupId);
    }

    @Override
    public List<Post> getPostsByUser(Long id) {
        return postRepository.findByAuthor_id(id);
    }

    @Override
    public Post likePost(Long postId) {
        Post post = getPostById(postId);
        post.setLikeCount(post.getLikeCount() + 1);
        post.setIsLikedByCurrentUser(true);
        return postRepository.save(post);
    }

    @Override
    public Post unlikePost(Long postId) {
        Post post = getPostById(postId);
        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
        }
        post.setIsLikedByCurrentUser(false);
        return postRepository.save(post);
    }

    @Override
    public Post createPostWithMedia(String content, Long groupId, String mediaUrl, FileType mediaType, Long authorId) {
        Post post = new Post();
        post.setContent(content);

        // Récupérer l'utilisateur par son ID
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Récupérer le groupe par son ID
        Group group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        post.setGroup(group);

        post.setMediaUrl(mediaUrl);
        post.setMediaType(mediaType);
        post.setCreatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Override
    public Post createPostWithoutMedia(String content, Long groupId, Long authorId) {
        Post post = new Post();
        post.setContent(content);

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + authorId));
        post.setAuthor(author);

        Group group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        post.setGroup(group);

        post.setCreatedAt(LocalDateTime.now());
        post.setLikeCount(0);
        post.setIsLikedByCurrentUser(false);

        return postRepository.save(post);
    }
}
