package uz.dckroff.statisfy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.dckroff.statisfy.model.DeviceToken;
import uz.dckroff.statisfy.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    List<DeviceToken> findByUserAndIsActiveTrue(User user);
    
    Optional<DeviceToken> findByToken(String token);
    
    @Query("SELECT dt FROM DeviceToken dt WHERE dt.user.id = :userId AND dt.isActive = true")
    List<DeviceToken> findActiveTokensByUserId(Long userId);
    
    void deleteByToken(String token);
    
    boolean existsByToken(String token);
} 