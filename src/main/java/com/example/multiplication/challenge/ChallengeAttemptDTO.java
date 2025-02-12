package com.example.multiplication.challenge;

import lombok.Value;


/*
 * Attempt coming from the user
 * */
@Value
public class ChallengeAttemptDTO {
    int factorA, factorB;
    String userAlias;
    int guess;
}
