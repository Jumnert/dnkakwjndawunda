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
public class Industries {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "industry_name")
    private String industryName;

    @OneToMany(mappedBy = "industry")
    private Set<Jobs> job;
}
