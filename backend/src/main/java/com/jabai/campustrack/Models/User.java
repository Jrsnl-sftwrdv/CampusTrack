package com.jabai.campustrack.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.jabai.campustrack.Models.Enums.UserRole;
import org.hibernate.annotations.Generated;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "created_at", updatable = false, insertable = false)
  @Generated
  private LocalDateTime createdAt;

  // Core columns
  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_role", nullable = false)
  private UserRole userRole;

  // Worker dispatch columns (only meaningful for WORKER accounts)
  @Column(name = "available", nullable = false)
  private boolean available = false; // on duty or not

  @Column(name = "last_assigned_at")
  private LocalDateTime lastAssignedAt; // tie-breaker when distributing work

  protected User() { }

  public User(String name, String email, String password, UserRole userRole) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.userRole = userRole;
  }

  // Getters
  public Long getId() { return id; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getHashedPassword() { return password; }
  public UserRole getUserRole() { return userRole; }
  public boolean getAvailable() { return available; }
  public LocalDateTime getLastAssignedAt() { return lastAssignedAt; }

  //Setters
  public void setName(String value) { name = value; }
  public void setEmail(String value) { email = value; }
  public void setHashedPassword(String value) { password = value; }
  public void setUserRole(UserRole value) { userRole = value; }
  public void setAvailable(boolean value) { available = value; }
  public void setLastAssignedAt(LocalDateTime value) { lastAssignedAt = value; }
}