package com.cyberguard.chatbot.repository;

import com.cyberguard.chatbot.model.Account;
import com.cyberguard.chatbot.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    List<Account> findAllByStatus(Status status);
}
