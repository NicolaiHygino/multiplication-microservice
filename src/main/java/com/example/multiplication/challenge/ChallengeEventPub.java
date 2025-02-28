package com.example.multiplication.challenge;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ChallengeEventPub {
    private final AmqpTemplate amqpTemplate;
    private final String challengesTopicExchange;

    public ChallengeEventPub(
            final AmqpTemplate amqpTemplate,
            @Value("amqp.exchange.attempts") final String getChallengesTopicExchange) {
        this.amqpTemplate = amqpTemplate;
        this.challengesTopicExchange = getChallengesTopicExchange;
    }

    public void challengeSolved(final ChallengeAttempt challengeAttempt) {
        ChallengeSolvedEvent event = buildEvent(challengeAttempt);
        // Routing key is 'attempt.correct' or 'attempt.wrong'
        String routingKey = "attempt." + (event.isCorrect() ? "correct" : "wrong");
        amqpTemplate.convertAndSend(challengesTopicExchange, routingKey, event);
    }

    private ChallengeSolvedEvent buildEvent(final ChallengeAttempt attempt) {
        return new ChallengeSolvedEvent(attempt.getId(), attempt.isCorrect(),
                attempt.getFactorA(), attempt.getFactorB(), attempt.getUser().getId(),
                attempt.getUser().getAlias());
    }
}
