package tn.esprit.spring.connectn.Services.Implementation.groups;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.LiveComment;
import tn.esprit.spring.connectn.Entities.groups.LiveSession;
import tn.esprit.spring.connectn.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class LiveService {

    private final AtomicLong liveIdCounter = new AtomicLong(1);
   // private final AtomicLong commentIdCounter = new AtomicLong(1);

    // Stockage en mémoire comme recommandé
    private final Map<Long, LiveSession> activeLives = new ConcurrentHashMap<>();
   // private final Map<Long, List<LiveComment>> liveComments = new ConcurrentHashMap<>();

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    public LiveSession startLive(Long groupId, Long id) {
        // Vérifier si un live est déjà actif pour ce groupe
        Optional<LiveSession> existingLive = activeLives.values().stream()
                .filter(live -> live.getGroupId().equals(groupId) && live.isActive())
                .findFirst();

        if (existingLive.isPresent()) {
            throw new IllegalStateException("Un live est déjà actif dans ce groupe");
        }

        // Récupérer l'utilisateur
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créer une nouvelle session live
        Long liveId = liveIdCounter.getAndIncrement();
        LiveSession liveSession = new LiveSession(
                liveId,
                groupId,
                id,
                user.getUsername(),
                LocalDateTime.now(),
                true
        );

        // Stocker la session
        activeLives.put(liveId, liveSession);

        // Initialiser la liste des commentaires
      //  liveComments.put(liveId, new ArrayList<>());

        // Notifier tous les membres du groupe
        messagingTemplate.convertAndSend("/topic/group/" + groupId + "/live",
                Map.of("type", "LIVE_STARTED", "data", liveSession));

        return liveSession;
    }

    public void endLive(Long liveId, Long id) {
        LiveSession liveSession = activeLives.get(liveId);

        if (liveSession == null) {
            throw new RuntimeException("Session live non trouvée");
        }

        if (!liveSession.getBroadcasterId().equals(id)) {
            throw new RuntimeException("Seul le diffuseur peut terminer le live");
        }

        // Mettre à jour l'état
        liveSession.setActive(false);

        // Notifier tous les membres du groupe
        messagingTemplate.convertAndSend("/topic/group/" + liveSession.getGroupId() + "/live",
                Map.of("type", "LIVE_ENDED", "liveId", liveId));

        // Supprimer les commentaires comme demandé
       // liveComments.remove(liveId);
    }

  /*  public LiveComment addComment(Long liveId, Long senderId, String content) {
        if (!activeLives.containsKey(liveId) || !activeLives.get(liveId).isActive()) {
            throw new RuntimeException("Le live n'est pas actif");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        LiveSession liveSession = activeLives.get(liveId);

        LiveComment comment = new LiveComment(
                commentIdCounter.getAndIncrement(),
                content,
                senderId,
                sender.getUsername(),
                liveId,
                LocalDateTime.now()
        );

        // Ajouter le commentaire à la liste
        liveComments.get(liveId).add(comment);

        // Notifier les utilisateurs
        messagingTemplate.convertAndSend("/topic/group/" + liveSession.getGroupId() + "/live/comments", comment);

        return comment;
    }

    public List<LiveComment> getLiveComments(Long liveId) {
        if (!liveComments.containsKey(liveId)) {
            return Collections.emptyList();
        }
        return liveComments.get(liveId);
    }
*/
    public List<LiveSession> getActiveLivesByGroupId(Long groupId) {
        return activeLives.values().stream()
                .filter(live -> live.getGroupId().equals(groupId) && live.isActive())
                .collect(Collectors.toList());
    }

    public LiveSession getLiveSession(Long liveId) {
        return activeLives.get(liveId);
    }
}