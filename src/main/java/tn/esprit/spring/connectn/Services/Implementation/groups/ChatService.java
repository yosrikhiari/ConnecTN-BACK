package tn.esprit.spring.connectn.Services.Implementation.groups;




import tn.esprit.spring.connectn.DTO.groups.MessageDTO;
import tn.esprit.spring.connectn.Entities.User;
import tn.esprit.spring.connectn.Entities.groups.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.esprit.spring.connectn.Repository.UserRepository;
import tn.esprit.spring.connectn.Repository.groups.ChatRepository;
import tn.esprit.spring.connectn.Repository.groups.GroupsRepository;
import tn.esprit.spring.connectn.Repository.groups.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupsRepository groupRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Chat getOrCreateGroupChat(Long groupId) {
        Optional<Chat> existingChat = chatRepository.findByGroupGroupId(groupId);

        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        // Créer un nouveau chat pour le groupe
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        Chat newChat = new Chat();
        newChat.setName(group.getName() + " Chat");
        newChat.setGroup(group);
        return chatRepository.save(newChat);
    }

    public MessageDTO sendMessage(MessageDTO messageDTO) {
        // 1. Validation
        if (messageDTO.getSenderId() == null) {
            throw new IllegalArgumentException("senderId is required");
        }
        if (messageDTO.getGroupId() == null) {
            throw new IllegalArgumentException("groupId is required");
        }

        // 2. Trouver ou créer le chat
        Chat chat = getOrCreateGroupChat(messageDTO.getGroupId());

        // 3. Sender
        User sender = userRepository.findById(messageDTO.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Créer le message
        Message message = new Message();
        message.setContent(messageDTO.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setSender(sender);
        message.setChat(chat); // Utilisez le chat trouvé

        Message savedMessage = messageRepository.save(message);

        // 5. Retourner la réponse
        return convertToDTO(savedMessage);
    }

    public List<MessageDTO> getGroupMessages(Long groupId) {
        Chat chat = getOrCreateGroupChat(groupId);
        List<Message> messages = messageRepository.findByChatChatIdOrderByTimestampAsc(chat.getChatId());

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());

        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setSenderId(message.getSender().getId());

        dto.setSenderName(message.getSender().getUsername());
        dto.setChatId(message.getChat().getChatId());
        dto.setGroupId(message.getChat().getGroup().getGroupId());
        return dto;

    }

    }

