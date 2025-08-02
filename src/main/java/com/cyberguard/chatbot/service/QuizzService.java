package com.cyberguard.chatbot.service;

import com.cyberguard.chatbot.model.Account;
import com.cyberguard.chatbot.model.Participants;
import com.cyberguard.chatbot.model.Status;
import com.cyberguard.chatbot.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class QuizzService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void saveResult(long score) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Participants participant =new Participants();

        participant.setName(username);
        participant.setScore(score);
        participant.setQuizzDate(LocalDate.now());
        participant.setConsent(true);
        participantRepository.save(participant);
    }



}
