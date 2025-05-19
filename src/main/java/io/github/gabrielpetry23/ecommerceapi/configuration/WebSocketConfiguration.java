package io.github.gabrielpetry23.ecommerceapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Define o endpoint para a conexão WebSocket. Clientes conectarão em /ws.
        // withSockJS() habilita o fallback para SockJS se o WebSocket não for suportado.
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilita um broker simples baseado em memória para enviar mensagens para destinos com o prefixo /topic.
        // Nossas notificações serão enviadas para /topic/user-notifications/{userId}.
        registry.enableSimpleBroker("/topic");

        // Define o prefixo para mensagens que os clientes enviam para o servidor.
        // Não usaremos para este caso de notificações do servidor para o cliente,
        // mas é uma boa prática configurar.
        registry.setApplicationDestinationPrefixes("/app");
    }
}