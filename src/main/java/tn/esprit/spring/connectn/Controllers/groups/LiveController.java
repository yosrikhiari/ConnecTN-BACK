package tn.esprit.spring.connectn.Controllers.groups;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.Entities.groups.LiveComment;
import tn.esprit.spring.connectn.Entities.groups.LiveSession;
import tn.esprit.spring.connectn.Services.Implementation.groups.LiveService;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/live")
public class LiveController {

    @Autowired
    private LiveService liveService;

    @PostMapping("/start")
    public ResponseEntity<LiveSession> startLive(@RequestBody Map<String, Long> payload) {
        Long groupId = payload.get("groupId");
        Long id = payload.get("id");

        if (groupId == null || id == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LiveSession liveSession = liveService.startLive(groupId, id);
            return ResponseEntity.ok(liveSession);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/end")
    public ResponseEntity<?> endLive(@RequestBody Map<String, Long> payload) {
        Long liveId = payload.get("liveId");
        Long id = payload.get("id");

        if (liveId == null || id == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            liveService.endLive(liveId, id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<LiveSession>> getGroupLives(@PathVariable Long groupId) {
        return ResponseEntity.ok(liveService.getActiveLivesByGroupId(groupId));
    }
/*
    @GetMapping("/{liveId}/comments")
    public ResponseEntity<List<LiveComment>> getLiveComments(@PathVariable Long liveId) {
        return ResponseEntity.ok(liveService.getLiveComments(liveId));
    }*/
    @GetMapping("/{liveId}")
    public ResponseEntity<LiveSession> getLiveById(@PathVariable Long liveId) {
        LiveSession liveSession = liveService.getLiveSession(liveId);
        if (liveSession == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(liveSession);
    }
}