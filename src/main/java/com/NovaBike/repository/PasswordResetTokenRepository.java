
package com.NovaBike.repository;

import com.NovaBike.domain.PasswordResetToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByToken(String token);
    
}
