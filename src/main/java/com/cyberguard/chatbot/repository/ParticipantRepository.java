package com.cyberguard.chatbot.repository;

import com.cyberguard.chatbot.model.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participants,Long> {
    List<Participants> findByQuizzDateBetween(LocalDate start, LocalDate end);

}
