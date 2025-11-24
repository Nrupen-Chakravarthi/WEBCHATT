    package com.example.webchat.Confg;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.simp.config.ChannelRegistration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Autowired // <-- Inject the new interceptor
        private StompChannelInterceptor stompChannelInterceptor;
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry){
            registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry registry){
            //prefix for client -> server
            registry.setApplicationDestinationPrefixes("/app");

            // prefix for server -> client
            // "/topic" is for the groups
            registry.enableSimpleBroker("/topic", "/queue");
            //queue is used for private messages
        }
        @Override
        public void configureClientInboundChannel(ChannelRegistration registration) {
            // Register the JWT interceptor to validate the token on connection and set the principal.
            registration.interceptors(stompChannelInterceptor);
        }
    }
