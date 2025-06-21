package uz.dckroff.statisfy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_preferences")
public class UserPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(length = 500)
    private String interests;
    
    @Column(name = "preferred_language")
    private String preferredLanguage;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_preferred_categories",
        joinColumns = @JoinColumn(name = "preference_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> preferredCategories = new HashSet<>();
} 