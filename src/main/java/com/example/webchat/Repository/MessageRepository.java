package com.example.webchat.Repository;

import com.example.webchat.Model.Message;
import com.example.webchat.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Import @Query
import org.springframework.data.repository.query.Param; // <-- Import @Param
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiver(User sender, User receiver);
    List<Message> findByReceiver(User receiver);

    // NEW METHOD: Finds all unique users who have communicated with the given user ID.
    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END " +
            "FROM Message m " +
            "WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findDistinctChatPartnerIds(@Param("userId") Long userId);
    // NOTE: The return type must be List<Long> since the query returns IDs.


    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = FALSE")
    long countUnreadMessages(
            @Param("receiverId") Long receiverId,
            @Param("senderId") Long senderId
    );

    // 2. FIND UNREAD MESSAGES (For clearing the chat when opened)
    List<Message> findByReceiverAndSenderAndIsReadFalse(User receiver, User sender);

    // 3. FIND PARTNER IDs (The stable query fix)

}