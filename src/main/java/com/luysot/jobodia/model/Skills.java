package com.luysot.jobodia.model;

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
public class Skills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_name", unique = true)
    private String skillName;

    @ManyToMany(mappedBy = "skills")
    private Set<SeekerProfiles> seekers = new HashSet<>();
}
