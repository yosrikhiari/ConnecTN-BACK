package tn.esprit.spring.connectn.Controllers.groups;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.LiveSession;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Services.Implementation.groups.LiveService;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin(origins = "*")
@Controller
public class LiveSocketController {

    @Autowired
    private LiveService liveService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/live.signal/{liveId}")
    @SendTo("/topic/group/+/live/signal")
    public Map<String, Object> handleSignal(@DestinationVariable Long liveId,
                                            @Payload Map<String, Object> signal) {
        // Récupérer la session live
        LiveSession liveSession = liveService.getLiveSession(liveId);
        if (liveSession == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Live session not found");
            return errorResponse;
        }

        // Si receiverId est -1, remplacer par l'ID du diffuseur
        if (signal.containsKey("receiverId") && ((Number)signal.get("receiverId")).longValue() == -1) {
            signal.put("receiverId", liveSession.getBroadcasterId());
        }

        // Ajout de logs pour le debug
        System.out.println("Signal forwarded: " + signal);

        return signal;
    }


}