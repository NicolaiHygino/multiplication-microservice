package com.example.multiplication.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

/*
 * Stores information to identify the user.
 * */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
  @Id @GeneratedValue private Long id;
  private String alias;

  public User(final String userAlias) {
    this(null, userAlias);
  }
}
