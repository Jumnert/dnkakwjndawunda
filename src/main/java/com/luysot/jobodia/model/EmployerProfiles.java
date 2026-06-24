package com.luysot.jobodia.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class EmployerProfiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;

    @Column(name = "company_logo")
    private String companyLogoUrl;
    @Column(name = "company_logo_stored_name")
    private String companyLogoStoredName;
    @Column(name = "company_logo_original_name")
    private String companyLogoOriginalName;
    @Column(name = "company_logo_content_type")
    private String companyLogoContentType;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @OneToMany(mappedBy = "employer")
    private Set<Jobs> jobs;
}
