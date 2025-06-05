package tn.esprit.spring.connectn.Controllers.groups;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.connectn.Entities.groups.FileType;
import tn.esprit.spring.connectn.Entities.groups.Post;
import tn.esprit.spring.connectn.Services.Implementation.groups.CloudinaryServiceGroup;
import tn.esprit.spring.connectn.Services.Interfaces.groups.IPostService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final IPostService postService;
    private final CloudinaryServiceGroup cloudinaryService;

    @Autowired
    public PostController(IPostService postService, CloudinaryServiceGroup cloudinaryServiceGroup) {
        this.postService = postService;
        this.cloudinaryService = cloudinaryServiceGroup;
    }

    // ============ ENDPOINTS CLOUDINARY ============
    @PostMapping(value = "/with-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Post> createPostWithMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("content") String content,
            @RequestParam("group") Long groupId,
            @RequestParam("authorId") Long authorId) {

        try {
            // Log received parameters
            System.out.println("Received media upload:");
            System.out.println("Content: " + content);
            System.out.println("Group ID: " + groupId);
            System.out.println("Author ID: " + authorId);
            System.out.println("File name: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());

            // Create post without media first to get ID
            Post newPost = postService.createPostWithoutMedia(content, groupId, authorId);

            // Upload file to specific folder
            String folder = "posts/" + newPost.getPostId();
            String mediaUrl = cloudinaryService.uploadFile(file, folder);

            // Determine media type
            FileType mediaType = determineFileType(file.getContentType());

            // Update post with media info
            newPost.setMediaUrl(mediaUrl);
            newPost.setMediaType(mediaType);
            Post updatedPost = postService.updatePost(newPost.getPostId(), newPost);

            return ResponseEntity.ok(updatedPost);
        } catch (IOException e) {
            System.err.println("Upload error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private FileType determineFileType(String contentType) {
        if (contentType == null) return FileType.TEXT;

        if (contentType.startsWith("image/")) {
            return FileType.IMAGE;
        } else if (contentType.startsWith("video/")) {
            return FileType.VIDEO;
        } else if (contentType.equals("application/pdf")) {
            return FileType.PDF;
        }
        return FileType.TEXT;
    }
    @PostMapping(value = "/upload-media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "postId", required = false) Long postId) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide");
        }

        try {
            String folder = "posts/" + (postId != null ? postId : "temp");
            String mediaUrl = cloudinaryService.uploadFile(file, folder);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("url", mediaUrl);
            response.put("publicId", cloudinaryService.extractPublicIdFromUrl(mediaUrl));

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "message", "Échec de l'upload: " + e.getMessage()
                    ));
        }
    }

    @PostMapping("/{postId}/update-media")
    public ResponseEntity<?> updatePostMedia(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file) {

        try {
            Post post = postService.getPostById(postId);

            // Suppression ancien média si existant
            if (post.getMediaUrl() != null) {
                try {
                    cloudinaryService.deleteImage(post.getMediaUrl());
                } catch (IOException e) {
                    // Log the error but continue with the update
                    System.err.println("Failed to delete old media: " + e.getMessage());
                }
            }

            // Upload nouveau média
            String folder = "posts/" + postId;
            String mediaUrl = cloudinaryService.uploadFile(file, folder);

            // Mise à jour du post
            post.setMediaUrl(mediaUrl);
            Post updatedPost = postService.updatePost(postId, post);

            return ResponseEntity.ok(updatedPost);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour du média: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur inattendue: " + e.getMessage());
        }
    }

    @DeleteMapping("/cleanup-media")
    public ResponseEntity<Void> cleanupMedia(@RequestParam String mediaUrl) {
        try {
            cloudinaryService.deleteImage(mediaUrl);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============ ENDPOINTS STANDARDS ============

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        return ResponseEntity.ok(postService.createPost(post));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody Post postDetails) {
        return ResponseEntity.ok(postService.updatePost(postId, postDetails));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        try {
            // Suppression du média associé si existant
            Post post = postService.getPostById(postId);
            if (post.getMediaUrl() != null) {
                try {
                    cloudinaryService.deleteImage(post.getMediaUrl());
                } catch (IOException e) {
                    // Log the error but continue with the deletion
                    System.err.println("Failed to delete media: " + e.getMessage());
                }
            }

            postService.deletePost(postId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============ ENDPOINTS SPÉCIFIQUES ============

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Post>> getPostsByGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(postService.getPostsByGroup(groupId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<Post> likePost(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(postService.likePost(postId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Post> unlikePost(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(postService.unlikePost(postId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}