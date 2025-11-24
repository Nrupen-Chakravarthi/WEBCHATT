package com.example.webchat.Controller;

import com.example.webchat.Model.Message;
import com.example.webchat.Model.User;
import com.example.webchat.Repository.MessageRepository;
import com.example.webchat.Repository.UserRepository;
import com.example.webchat.Service.MessageService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
//@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST})
public class MessageController {
    @Autowired
    private MessageService service;

    @Autowired
    private UserRepository repo;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{senderUsername}/{receiverUsername}")
    public ResponseEntity<?> getMessages(@PathVariable String senderUsername, @PathVariable String receiverUsername){
        User sender = repo.findByUsername(senderUsername).orElse(null);
        User receiver = repo.findByUsername(receiverUsername).orElse(null);

        if(sender == null || receiver == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Sender or Receiver not found");
        }
        List<Message> chat = messageRepository.findBySenderAndReceiver(sender, receiver);

        if(chat.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getAllMessagesForUser(@RequestParam String username){
        User user = repo.findByUsername(username).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        List<Message> chat = service.getAllMessages(user);
        if(chat.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(chat);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(Message chatMessage){

        String senderUsername = chatMessage.getSender().getUsername();
        String receiverUsername = chatMessage.getReceiver().getUsername();

        User managedSender = repo.findByUsername(senderUsername).orElseThrow(() -> new IllegalArgumentException("Sender not found: "+ senderUsername));
        User managedReciver = repo.findByUsername(receiverUsername).orElseThrow(() -> new IllegalArgumentException("Receiver not found: "+ receiverUsername));

        chatMessage.setSender(managedSender);
        chatMessage.setReceiver(managedReciver);

        LocalDateTime now = LocalDateTime.now();

        chatMessage.setTimestamp(java.time.LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute()));

        //save the message in the db
        messageRepository.save(chatMessage);
        System.out.println("Sender: " + chatMessage.getSender().getUsername());
        System.out.println("Receiver: " + chatMessage.getReceiver().getUsername());

        //send message only to receiver now
        template.convertAndSendToUser(
                chatMessage.getReceiver().getUsername(),
                "/queue/messages",
                chatMessage
        );
    }
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam String username) {
        List<User> users = repo.findByUsernameContaining(username);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/recent-chats")
    public ResponseEntity<List<User>> getRecentChats(java.security.Principal principal) {
        // 1. Get the current user's details (needed to get the user's ID)
        String currentUsername = principal.getName();
        User currentUser = repo.findByUsername(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + currentUsername));

        // 2. Use the service to fetch distinct partners by the user's ID
        List<User> partners = service.getRecentChatPartners(currentUser.getId());

        return ResponseEntity.ok(partners);
    }

}
