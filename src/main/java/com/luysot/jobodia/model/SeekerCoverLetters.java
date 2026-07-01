package com.luysot.jobodia.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class SeekerCoverLetters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "coverLetter")
    private String coverLetterUrl;
    @Column(name = "coverLetter_stored_name")
    private String coverLetterStoredName;
    @Column(name = "coverLetter_original_name")
    private String coverLetterOriginalName;
    @Column(name = "coverLetter_content_type")
    private String coverLetterContentType;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "seeker_id")
    private SeekerProfiles seeker;

    @OneToMany(mappedBy = "coverLetter", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Applications> applications;
}
