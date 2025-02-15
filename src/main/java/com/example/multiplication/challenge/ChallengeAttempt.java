package com.example.multiplication.challenge;

import com.example.multiplication.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeAttempt {
  @Id @GeneratedValue private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USER_ID")
  private User users;

  private int factorA;
  private int factorB;
  private int guess;
  private boolean correct;
}
