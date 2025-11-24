package com.example.webchat.Service;

import com.example.webchat.Model.Message;
import com.example.webchat.Model.User;
import com.example.webchat.Repository.MessageRepository;
import com.example.webchat.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository repo;

    @Autowired
    private UserRepository userRepository;

    //save the message
    public Message saveMessage(Message message){
        return repo.save(message);
    }

    public List<User> getRecentChatPartners(Long userId) {

        // 1. Fetch the unique IDs of the chat partners (unordered, but stable)
        List<Long> partnerIds = repo.findDistinctChatPartnerIds(userId);

        // 2. Fetch the full User objects corresponding to those IDs.
        List<User> partners = userRepository.findAllById(partnerIds);

        // The list is now stable but ordered by the User ID default.
        return partners;
    }
    //get all the messages between the sender and receiver
    public List<Message> getMessagesBetweenSenderAndReceiver(User sender, User receiver){
        return repo.findBySenderAndReceiver(sender, receiver);
    }

    //get all the messages for the user
    public List<Message> getAllMessages(User user){
        return repo.findByReceiver(user);
    }
}
