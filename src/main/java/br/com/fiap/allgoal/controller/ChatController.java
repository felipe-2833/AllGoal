package br.com.fiap.allgoal.controller;

import br.com.fiap.allgoal.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    public record ChatRequest(String message) {}

    @PostMapping
    public Map<String, String> sendMessage(@RequestBody ChatRequest request) {
        String response = chatService.sendMessage(request.message());
        return Map.of("response", response);
    }
}