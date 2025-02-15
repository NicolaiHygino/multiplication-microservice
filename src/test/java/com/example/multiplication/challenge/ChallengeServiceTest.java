package com.example.multiplication.challenge;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;

import com.example.multiplication.user.User;
import com.example.multiplication.user.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    BDDMockito.given(challengeAttemptRepository.save(any())).will(returnsFirstArg());
  }

  @Test
  public void checkCorrectAttemptTest() {
    // given
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
    ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 60, "nicolai", 5000);
    // when
    ChallengeAttempt resultAttempt = challengeService.verifyAttempt(attemptDTO);
    // then
    then(resultAttempt.isCorrect()).isFalse();
  }

  @Test
  public void checkExistingUserTest() {
    // given
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
}
