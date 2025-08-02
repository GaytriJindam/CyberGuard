package com.cyberguard.chatbot.controller;

import com.cyberguard.chatbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    ChatService chatService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> payload) {
        String userInput = payload.get("message");
        String response = chatService.getResponse(String.valueOf(userInput));
        System.out.print("response   "+response);
        return ResponseEntity.ok(Map.of("response", response));
    }


}
