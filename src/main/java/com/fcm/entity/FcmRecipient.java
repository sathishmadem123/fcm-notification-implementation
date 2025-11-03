package com.fcm.entity;

import com.fcm.entity.audit.AuditModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"recipient_id", "token"})
        }
)
public class FcmRecipient extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false)
    private Long recipientId;

    @Column(updatable = false, nullable = false, unique = true)
    private String token;

    @ManyToMany
    @JoinTable(
            name = "recipient_topic",
            joinColumns = @JoinColumn(name = "recipient_id"),
            inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    private List<FcmTopic> topics = new ArrayList<>();
}
