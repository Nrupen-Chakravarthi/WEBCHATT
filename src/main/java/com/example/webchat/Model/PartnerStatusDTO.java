package com.example.webchat.Model;
import java.time.LocalDateTime;
public class PartnerStatusDTO {

        private Long id;
        private String username;
        private LocalDateTime lastSeen;
        private long unreadCount; // Tracks unread count

        public PartnerStatusDTO(User user, long unreadCount) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.lastSeen = user.getLastSeen();
            this.unreadCount = unreadCount;
        }

        // --- Standard Getters and Setters ---
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public LocalDateTime getLastSeen() { return lastSeen; }
        public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
        public long getUnreadCount() { return unreadCount; }
        public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
    }

