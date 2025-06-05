package tn.esprit.spring.connectn.Controllers.groups;

import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.*;
import tn.esprit.spring.connectn.Services.Implementation.groups.CloudinaryServiceGroup;
import tn.esprit.spring.connectn.Services.Interfaces.groups.IGroupsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/groups")
public class GroupsController {

    private final IGroupsService groupsService;
    private final CloudinaryServiceGroup cloudinaryServiceGroup;

    @Autowired
    public GroupsController(IGroupsService groupService, CloudinaryServiceGroup cloudinaryServiceGroup) {
        this.groupsService = groupService;
        this.cloudinaryServiceGroup = cloudinaryServiceGroup;
    }

    // ============ ENDPOINTS D'UPLOAD D'IMAGES ============


    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folderType") String folderType) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide");
        }

        try {
            String folder = "groups/temp";
            String imageUrl = cloudinaryServiceGroup.uploadFile(file, folder);

            Map<String, String> response = new HashMap<>();
            response.put("url", imageUrl);
            response.put("publicId", cloudinaryServiceGroup.extractPublicIdFromUrl(imageUrl));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur upload: " + e.getMessage());
        }
    }
    @PostMapping("/{groupId}/cover-image")
    public ResponseEntity<?> uploadCoverImage(
            @PathVariable Long groupId,
            @RequestParam("file") MultipartFile file) {

        try {
            Group group = groupsService.getGroupById(groupId);

            // Suppression ancienne image
            if (group.getCoverPhoto() != null) {
                cloudinaryServiceGroup.deleteImage(group.getCoverPhoto());
            }

            // Upload nouvelle image
            String folder = "groups/" + groupId + "/covers";
            String imageUrl = cloudinaryServiceGroup.uploadFile(file, folder);

            group.setCoverPhoto(imageUrl);
            return ResponseEntity.ok(groupsService.updateGroup(groupId, group));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur image de couverture: " + e.getMessage());
        }
    }

    @PostMapping("/{groupId}/group-image")
    public ResponseEntity<?> uploadGroupImage(
            @PathVariable Long groupId,
            @RequestParam("file") MultipartFile file) {

        try {
            Group group = groupsService.getGroupById(groupId);

            // Suppression ancienne image
            if (group.getGroupImage() != null) {
                cloudinaryServiceGroup.deleteImage(group.getGroupImage());
            }

            // Upload nouvelle image
            String folder = "groups/" + groupId + "/profiles";
            String imageUrl = cloudinaryServiceGroup.uploadFile(file, folder);

            group.setGroupImage(imageUrl);
            return ResponseEntity.ok(groupsService.updateGroup(groupId, group));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur image du groupe: " + e.getMessage());
        }
    }

    @DeleteMapping("/cleanup-image")
    public ResponseEntity<Void> cleanupImage(@RequestParam String imageUrl) {
        try {
            cloudinaryServiceGroup.deleteImage(imageUrl);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============ ENDPOINTS STANDARDS ============

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        return ResponseEntity.ok(groupsService.createGroup(group));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroup(@PathVariable Long id) {
        return ResponseEntity.ok(groupsService.getGroupById(id));
    }

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        return ResponseEntity.ok(groupsService.getAllGroups());
    }

    @GetMapping("/my/{id}")
    public ResponseEntity<List<Group>> getMyGroups(@PathVariable Long id) {
        return ResponseEntity.ok(groupsService.getMyGroups(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Group>> getGroupsByType(@PathVariable GroupType type) {
        return ResponseEntity.ok(groupsService.getGroupsByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable Long id, @RequestBody Group groupDetails) {
        return ResponseEntity.ok(groupsService.updateGroup(id, groupDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupsService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Group> addMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return ResponseEntity.ok(groupsService.addMember(groupId, userId));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Group> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        return ResponseEntity.ok(groupsService.removeMember(groupId, userId));
    }

    @GetMapping("/{groupId}/pending-requests")
    public ResponseEntity<Map<User, JoinStatus>> getPendingRequests(@PathVariable Long groupId) {
        return ResponseEntity.ok(groupsService.getPendingRequests(groupId));
    }

    @PostMapping("/{groupId}/approve-request/{userId}")
    public ResponseEntity<Group> approveRequest(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(groupsService.approveRequest(groupId, userId));
    }

    @PostMapping("/{groupId}/reject-request/{userId}")
    public ResponseEntity<Group> rejectRequest(
            @PathVariable Long groupId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(groupsService.rejectRequest(groupId, userId));
    }
}