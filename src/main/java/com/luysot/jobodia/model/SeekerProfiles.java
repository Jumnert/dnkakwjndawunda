package com.luysot.jobodia.model;

import com.luysot.jobodia.model.enums.UserGender;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class SeekerProfiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_picture")
    private String profilePictureUrl;
    @Column(name = "profile_picture_stored_name")
    private String profilePictureStoredName;
    @Column(name = "profile_picture_original_name")
    private String profilePictureOriginalName;
    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    @Column(name = "gender")
    @Enumerated(value = EnumType.STRING)
    private UserGender gender;

    @Column(name = "address")
    private String address;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @ManyToMany
    @JoinTable(
            name = "seeker_skill",
            joinColumns = @JoinColumn(name = "seeker_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skills> skills = new HashSet<>();

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeekerResumes> resumes;

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SeekerCoverLetters> coverLetters;

    @OneToMany(mappedBy = "seeker", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Applications> applications;
}
