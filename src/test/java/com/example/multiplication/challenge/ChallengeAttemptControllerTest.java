package com.example.multiplication.challenge;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.example.multiplication.user.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@AutoConfigureJsonTesters
@WebMvcTest(ChallengeAttemptController.class)
public class ChallengeAttemptControllerTest {
  @MockBean private ChallengeService challengeService;

  @Autowired private MockMvc mvc;

  @Autowired private JacksonTester<ChallengeAttemptDTO> jsonRequestAttempt;

  @Autowired private JacksonTester<ChallengeAttempt> jsonResultAttempt;

  @Autowired private JacksonTester<List<ChallengeAttempt>> jsonResultAttemptList;

  @Test
  void postValidResult() throws Exception {
    // given
    User user = new User(1L, "nicolai");
    long attemptId = 5L;
    ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(50, 70, "john", 3500);
    ChallengeAttempt expectedResponse = new ChallengeAttempt(attemptId, user, 50, 70, 3500, true);
    given(challengeService.verifyAttempt(eq(attemptDTO))).willReturn(expectedResponse);
    // when
    MockHttpServletResponse response =
        mvc.perform(
                post("/attempts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequestAttempt.write(attemptDTO).getJson()))
            .andReturn()
            .getResponse();
    // then
    then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    then(response.getContentAsString())
        .isEqualTo(jsonResultAttempt.write(expectedResponse).getJson());
  }

  @Test
  void postInvalidResult() throws Exception {
    // given an attempt with invalid input data
    ChallengeAttemptDTO attemptDTO = new ChallengeAttemptDTO(2000, -70, "nicolai", 1);

    // when
    MockHttpServletResponse response =
        mvc.perform(
                MockMvcRequestBuilders.post("/attempts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequestAttempt.write(attemptDTO).getJson()))
            .andReturn()
            .getResponse();

    // then
    then(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void getLastAttempts() throws Exception {
    // given
    User user = new User(1L, "nicolai");
    ChallengeAttempt attempt1 = new ChallengeAttempt(1L, user, 50, 60, 3000, true);
    ChallengeAttempt attempt2 = new ChallengeAttempt(2L, user, 30, 40, 12000, false);
    List<ChallengeAttempt> expectedAttempts = List.of(attempt1, attempt2);

    given(challengeService.lastAttempts("nicolai")).willReturn(expectedAttempts);

    // when
    MockHttpServletResponse response =
        mvc.perform(
                get("/attempts").param("alias", "nicolai").contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse();

    // then
    then(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    then(response.getContentAsString())
        .isEqualTo(jsonResultAttemptList.write(expectedAttempts).getJson());
  }
}
