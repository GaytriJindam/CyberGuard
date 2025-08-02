package com.cyberguard.chatbot.controller;



import com.cyberguard.chatbot.model.Participants;
import com.cyberguard.chatbot.model.Question;
import com.cyberguard.chatbot.repository.ParticipantRepository;
import com.cyberguard.chatbot.repository.QuestionRepository;
import com.cyberguard.chatbot.service.QuizzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
public class QuizController {

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private QuizzService quizzService;

    @GetMapping("/quiz")
    public String showQuiz(Model model) {
        List<Question> questions = questionRepository.findAll();
        model.addAttribute("questions", questions);
        return "quiz";
    }

    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Map<String, String> allParams, Model model) {
        int score = 0;
        int total = 0;

        List<Question> questions = questionRepository.findAll();
        for (Question question : questions) {
            String selected = allParams.get("question_" + question.getId());
            if (selected != null && selected.equalsIgnoreCase(question.getCorrectAnswer())) {
                score++;
            }
            total++;
        }

        quizzService.saveResult(score);
        model.addAttribute("score", score);
        model.addAttribute("total", total);
        return "result";
    }

    @GetMapping("/participants/by-date")
    @ResponseBody
    public List<Participants> getByDateRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        List<Participants> participantsList = participantRepository.findByQuizzDateBetween(start, end);
        System.out.println("participantsList "+Arrays.asList(participantsList));
        return participantsList;
    }


}
