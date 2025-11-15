package br.com.fiap.allgoal.service;

import br.com.fiap.allgoal.tools.PerformanceTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final PerformanceTools performanceTools;
    private final ChatMemory chatMemory;

    public ChatService(ChatClient.Builder builder, PerformanceTools performanceTools) {
        this.performanceTools = performanceTools;
        this.chatMemory = MessageWindowChatMemory.builder().maxMessages(20).build();

        this.chatClient = builder
                .defaultSystem(
                        """
                        Você é o "AllGoal Coach", um assistente de performance e mentor. 
                        Seu único objetivo é ajudar o funcionário a atingir seu potencial máximo.
                        Você é positivo, motivador e estratégico.
                        
                        NUNCA responda perguntas que não sejam sobre as metas, recompensas ou 
                        performance do funcionário. Se o usuário perguntar sobre o tempo 
                        ou qualquer outra coisa, responda educadamente que o seu foco é 
                        apenas no seu desenvolvimento profissional.
                        
                        Ao criar planos, use as ferramentas para ver o status atual do usuário 
                        e as metas e recompensas disponíveis. Seja específico.
                        """
                )

                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String sendMessage(String message) {
        return chatClient.prompt()
                .user(message)
                .tools(performanceTools)
                .call()
                .content();
    }
}