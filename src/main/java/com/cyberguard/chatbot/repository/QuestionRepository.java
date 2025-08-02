package com.cyberguard.chatbot.repository;

import com.cyberguard.chatbot.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
