package com.example.multiplication.challenge;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * This class provides a REST API to POST the attempts from users.
 * */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/attempts")
public class ChallengeAttemptController {
  private final ChallengeService challengeService;

  @PostMapping
  ResponseEntity<ChallengeAttempt> postResult(
      @RequestBody @Valid ChallengeAttemptDTO challengeAttemptDTO) {
    return ResponseEntity.ok(challengeService.verifyAttempt(challengeAttemptDTO));
  }

  @GetMapping("/statistics")
  ResponseEntity<List<ChallengeAttempt>> getStatistics(@RequestParam String alias) {
    return ResponseEntity.ok(challengeService.getStatsForUser(alias));
  }

  @GetMapping("/last-attempts")
  ResponseEntity<List<ChallengeAttempt>> lastAttempts(@RequestParam String alias) {
    return ResponseEntity.ok(challengeService.lastAttempts(alias));
  }
}
