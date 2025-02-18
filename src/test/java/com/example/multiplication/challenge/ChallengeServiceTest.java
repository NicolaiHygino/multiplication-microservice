package com.example.multiplication.challenge;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;

import com.example.multiplication.user.User;
import com.example.multiplication.user.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChallengeServiceTest {
  private ChallengeService challengeService;

  @Mock private UserRepository userRepository;
  @Mock private ChallengeAttemptRepository challengeAttemptRepository;

  @BeforeEach
  public void setUp() {

    challengeService = new ChallengeServiceImpl(userRepository, challengeAttemptRepository);
  }

  private void mockSaveChallengeAttempt() {
    BDDMockito.given(challengeAttemptRepository.save(any()))
        .will(AdditionalAnswers.returnsFirstArg());
  }

  @Test
  public void checkCorrectAttemptTest() {
    // given
    mockSaveChallengeAttempt();
    ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "nicolai", 3000);
    // when
    ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);
    // then
    then(resultAttempt.isCorrect()).isTrue();
    Mockito.verify(userRepository).save(new User("nicolai"));
    Mockito.verify(challengeAttemptRepository).save(resultAttempt);
  }

  @Test
  public void checkWrongAttemptTest() {
    // given
    mockSaveChallengeAttempt();
    ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "nicolai", 5000);
    // when
    ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);
    // then
    then(resultAttempt.isCorrect()).isFalse();
  }

  @Test
  public void checkExistingUserTest() {
    // given
    mockSaveChallengeAttempt();
    User existingUser = new User(1L, "nicolai");
    BDDMockito.given(userRepository.findByAlias("nicolai")).willReturn(Optional.of(existingUser));
    ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "nicolai", 5000);
    // when
    ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);
    // then
    then(resultAttempt.isCorrect()).isFalse();
    then(resultAttempt.getUser()).isEqualTo(existingUser);
    Mockito.verify(userRepository, Mockito.never()).save(any());
    Mockito.verify(challengeAttemptRepository).save(resultAttempt);
  }

  @Test
  public void checkReturnLastAttempts() {
    // given
    User user = new User("nicolai");
    ChallengeAttempt attempt1 = new ChallengeAttempt(1L, user, 50, 60, 3000, true);
    ChallengeAttempt attempt2 = new ChallengeAttempt(2L, user, 50, 60, 5000, false);
    List<ChallengeAttempt> lastAttempts = List.of(attempt1, attempt2);
    BDDMockito.given(challengeAttemptRepository.lastAttempts("nicolai")).willReturn(lastAttempts);
    // when
    List<ChallengeAttempt> resultAttempt = challengeService.lastAttempts("nicolai");
    // then
    then(resultAttempt).isEqualTo(lastAttempts);
  }
}
