package uz.dckroff.statisfy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.User;
import uz.dckroff.statisfy.model.UserPreference;

import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUser(User user);
    Optional<UserPreference> findByUserId(Long userId);
    void deleteByUser(User user);
} 