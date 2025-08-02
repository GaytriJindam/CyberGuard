package com.cyberguard.chatbot.repository;

import com.cyberguard.chatbot.model.ChatDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatDTO, Long> {
   // List<ChatDTO> findByQuestionContainingIgnoreCase(String question);
    List<ChatDTO> findByQuestionIgnoreCase(String question);

    @Query("SELECT c FROM ChatDTO c WHERE LOWER(c.question) LIKE LOWER(CONCAT('% ', :word, ' %'))")
    List<ChatDTO> findByQuestionContainingWord(@Param("word") String word);


}
