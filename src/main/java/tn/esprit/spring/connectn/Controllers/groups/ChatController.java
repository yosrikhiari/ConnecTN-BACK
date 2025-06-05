package tn.esprit.spring.connectn.Controllers.groups;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.connectn.DTO.groups.MessageDTO;
import tn.esprit.spring.connectn.Services.Implementation.groups.ChatService;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat.sendMessage/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public MessageDTO sendMessage(@DestinationVariable Long groupId, @Payload MessageDTO messageDTO) {
        return chatService.sendMessage(messageDTO);
    }

    @GetMapping("/group/{groupId}")
    public List<MessageDTO> getGroupMessages(@PathVariable Long groupId) {
        return chatService.getGroupMessages(groupId);
    }
}
