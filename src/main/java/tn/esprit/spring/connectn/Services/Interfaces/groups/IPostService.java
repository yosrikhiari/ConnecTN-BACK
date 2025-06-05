package tn.esprit.spring.connectn.Services.Interfaces.groups;


import tn.esprit.spring.connectn.Entities.groups.FileType;
import tn.esprit.spring.connectn.Entities.groups.Post;

import java.util.List;

public interface IPostService {
    Post createPost(Post post);
    Post createPostWithMedia(String content, Long groupId, String mediaUrl, FileType mediaType, Long authorId);
    Post createPostWithoutMedia(String content, Long groupId, Long authorId);
    Post getPostById(Long postId);
    List<Post> getAllPosts();
    Post updatePost(Long postId, Post postDetails);
    void deletePost(Long postId);
    List<Post> getPostsByGroup(Long groupId);
    List<Post> getPostsByUser(Long userId);
    Post likePost(Long postId);
    Post unlikePost(Long postId);
}
