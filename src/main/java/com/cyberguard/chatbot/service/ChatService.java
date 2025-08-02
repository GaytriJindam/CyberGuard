package com.cyberguard.chatbot.service;

import com.cyberguard.chatbot.model.ChatDTO;
import com.cyberguard.chatbot.repository.ChatRepository;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;


import java.io.InputStream;
import java.util.Arrays;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    @Autowired
    ChatRepository chatRepository;

    private static final List<String> PHISHING_KEYWORDS = Arrays.asList(
            "urgent", "verify your account", "login now", "suspended", "click here",
            "update your information", "password expired", "unauthorized access", "reset your password",
            "confirm your identity", "you have won", "free gift", "limited time offer"
    );

    private static final List<String> EMAIL_PHRASES = Arrays.asList(
            "dear", "hello", "hi", "regards", "sincerely", "thank you",
            "please find", "this is to inform you", "best regards", "contact us",
            "login to your account", "verify your email","PFA"
    );


    public String getResponse(String userInput) {
        List<ChatDTO> possibleMatches= null;
        List<String> links=extractLinks(userInput);
        boolean isEmail=isLikelyEmailContent(userInput);
        if(!links.isEmpty() )
        {
             userInput= "phishing prevention";
           boolean status = isPhishingLink(links.get(0));
           if(status) {
               possibleMatches = chatRepository.findByQuestionIgnoreCase(userInput);
               return "Yes it is phishing link \n Below are the steps to follow \n" +
                       possibleMatches.get(0).getAnswer();
           }
           else {
               return "This link appears safe.";
           }
        }else if(isEmail){
        boolean status= isPhishingEmail(userInput);
            userInput= "phishing prevention";
            if(status) {
                possibleMatches = chatRepository.findByQuestionIgnoreCase(userInput);
                return "Yes it is phishing email \n Below are the steps to follow \n" +
                        possibleMatches.get(0).getAnswer();
            }
            else{
                return  "This email appears safe.";
            }
            }else if(userInput.contains("@")){

            boolean isInvalidEmail=isInvalidEmail(userInput);
            userInput= "phishing prevention";
            if(isInvalidEmail) {
                possibleMatches = chatRepository.findByQuestionIgnoreCase(userInput);
                return "Yes it is phishing email ID\n Below are the steps to follow \n" +
                        possibleMatches.get(0).getAnswer();
            }
            else{
                return  "This email appears safe.";
            }
        }
        possibleMatches = chatRepository.findByQuestionIgnoreCase(userInput);
        if (possibleMatches.isEmpty()) {
            String[] words = userInput.split(" ");
            for(String word:words) {
                possibleMatches = chatRepository.findByQuestionContainingWord(word);
                if(!possibleMatches.isEmpty())
                    return possibleMatches.get(0).getAnswer();
            }
        }else{
            return possibleMatches.get(0).getAnswer();

        }

        return "Sorry, I couldn't understand. Please rephrase or ask something else about phishing.";
    }

    public static List<String> extractLinks(String message) {
        List<String> links = new ArrayList<>();

        // Regex pattern to match most URLs
        String regex = "\\b(https?|ftp|file):\\/\\/[-A-Z0-9+&@#/%?=~_|!:,.;]*[-A-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            links.add(matcher.group());
        }

        return links;
    }
    public static boolean isInvalidEmail(String email) {
        try {
            // Corrected regex: only one escaped backslash before the dot, no \n
            String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z]+\\.[A-Za-z]{2,}$";
            return !email.matches(regex); // return true if email is invalid
        } catch (Exception ex) {
            return true;
        }
    }



    public static boolean isPhishingLink(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost().toLowerCase();

            // Rule 1: IP address instead of domain name
            if (host.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
                return true;
            }

            if (host.split("\\.").length > 3) {
                return true;
            }

            String[] suspiciousBrands = {"paypal", "apple", "google", "microsoft", "facebook"};
            for (String brand : suspiciousBrands) {
                String regex = ".*" + brand.replaceAll(".", "[a-zA-Z0-9]?") + ".*";
                if (host.contains(regex) || !host.endsWith(brand + ".com")) {
                    return true;
                }
            }

            if (host.contains("-" + "paypal") || host.contains("paypal" + "-")) {
                return true;
            }

            String[] knownShorteners = {"bit.ly", "tinyurl.com", "t.co", "goo.gl"};
            for (String shortener : knownShorteners) {
                if (host.equals(shortener)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isLikelyEmailContent(String content) {
        String lowercaseContent = content.toLowerCase();

        int matchCount = 0;
        for (String phrase : EMAIL_PHRASES) {
            if (lowercaseContent.contains(phrase)) {
                matchCount++;
            }
        }

        // Consider it email-like if several common phrases are found
        return matchCount >= 2;
    }

    public static boolean isPhishingEmail(String email) {
        String content = email.toLowerCase();
        for (String keyword : PHISHING_KEYWORDS) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }


//    public String getResponse(String userInput) {
//        List<ChatDTO> possibleMatches = new ArrayList<>();
//
//        try {
//            // Load models
//            InputStream tokenModelIn = getClass().getResourceAsStream("/en-token.bin");
//            InputStream lemmaDictIn = getClass().getResourceAsStream("/en-lemmatizer.dict");
//            InputStream posModelIn = getClass().getResourceAsStream("/da-pos-maxent.bin");
//
//            if (tokenModelIn == null || lemmaDictIn == null || posModelIn == null) {
//                throw new RuntimeException("One or more model files not found in resources.");
//            }
//
//            // Initialize OpenNLP components
//            TokenizerModel tokenModel = new TokenizerModel(tokenModelIn);
//            TokenizerME tokenizer = new TokenizerME(tokenModel);
//
//            POSModel posModel = new POSModel(posModelIn);
//            POSTaggerME posTagger = new POSTaggerME(posModel);
//
//            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(lemmaDictIn);
//
//            // Tokenize and lemmatize input
//            String[] tokens = tokenizer.tokenize(userInput);
//            String[] posTags = posTagger.tag(tokens);
//            String[] lemmas = lemmatizer.lemmatize(tokens, posTags);
//
//            // Normalize query
//            String normalizedQuery = String.join(" ", lemmas).toLowerCase();
//
//            // Match against stored questions
//            List<ChatDTO> allResponses = chatRepository.findAll();
//
//            for (ChatDTO response : allResponses) {
//                String[] responseTokens = tokenizer.tokenize(response.getQuestion());
//                System.out.println("responseTokens  "+Arrays.toString(responseTokens));
//                String[] responsePosTags = posTagger.tag(responseTokens);
//                System.out.println("responsePosTags "+Arrays.toString(responsePosTags));
//                String[] responseLemmas = lemmatizer.lemmatize(responseTokens, responsePosTags);
//                System.out.println("responseLemmas "+Arrays.toString(responseLemmas));
//                int matchScore = calculateMatchScore(lemmas, responseLemmas);
//                if (matchScore > 0) {
//                    response.setMatchScore(matchScore); // Must be transient field in ChatDTO
//                    possibleMatches.add(response);
//                }
//            }
//
//            if (!possibleMatches.isEmpty()) {
//                possibleMatches.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
//                return possibleMatches.get(0).getAnswer();
//            }
//System.out.println("normalizedQuery  "+normalizedQuery);
//            // Fallback to substring match
//            possibleMatches = chatRepository.findByQuestionContainingIgnoreCase(normalizedQuery);
//            if (!possibleMatches.isEmpty()) {
//                return possibleMatches.get(0).getAnswer();
//            }
//
//        } catch (Exception e) {
//            System.err.println("NLP processing failed: " + e.getMessage());
//            e.printStackTrace();
//
//            // Final fallback
//            possibleMatches = chatRepository.findByQuestionContainingIgnoreCase(userInput);
//            if (!possibleMatches.isEmpty()) {
//                return possibleMatches.get(0).getAnswer();
//            }
//        }
//
//        return "Sorry, I couldn't understand. Please rephrase or ask something else about phishing.";
//    }
//
//    private int calculateMatchScore(String[] inputLemmas, String[] responseLemmas) {
//        Set<String> inputSet = new HashSet<>(Arrays.asList(inputLemmas));
//        Set<String> responseSet = new HashSet<>(Arrays.asList(responseLemmas));
//        inputSet.remove("O"); // remove unknowns
//        responseSet.remove("O");
//
//        Set<String> intersection = new HashSet<>(inputSet);
//        intersection.retainAll(responseSet);
//
//        return intersection.size();
//    }
}
