package com.luysot.jobodia.model;

import com.luysot.jobodia.model.enums.JobGender;
import com.luysot.jobodia.model.enums.JobLevel;
import com.luysot.jobodia.model.enums.JobSite;
import com.luysot.jobodia.model.enums.JobTime;
import com.luysot.jobodia.model.enums.JobStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Jobs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "min_salary",precision = 10, scale = 2)
    private BigDecimal minSalary;

    @Column(name = "max_salary",precision = 10, scale = 2)
    private BigDecimal maxSalary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> responsibilities;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> requirements;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> benefits;

    @Column(name = "job_type")
    @Enumerated(value = EnumType.STRING)
    private JobTime jobType;

    @Column(name = "job_level")
    @Enumerated(value = EnumType.STRING)
    private JobLevel jobLevel;

    @Column(name = "job_gender")
    @Enumerated(value = EnumType.STRING)
    private JobGender jobGender;

    @Column(name = "job_site")
    @Enumerated(value = EnumType.STRING)
    private JobSite jobSite;

    @Column(name = "job_status")
    @Enumerated(value = EnumType.STRING)
    private JobStatus status = JobStatus.DRAFT;

    @Column(name = "years_of_experience")
    private Long yearsOfExperience;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> language;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<String> qualification;

    private Integer availablePosition;

    @CreationTimestamp
    private Timestamp createdAt;

    private LocalDateTime expireAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private Industries industry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private EmployerProfiles employer;

    @ManyToMany
    @JoinTable(
            name = "job_category",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Categories> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skills> skills = new HashSet<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Applications> applications;
}
