package com.certified.fullstack.quiz.entity;

import com.certified.fullstack.notification.NotificationStatus;
import com.certified.fullstack.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL)
    private List<AttemptAnswer> answers;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;
}