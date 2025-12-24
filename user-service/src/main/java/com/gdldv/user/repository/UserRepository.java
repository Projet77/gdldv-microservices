package com.gdldv.user.repository;

import com.gdldv.user.entity.Role;
import com.gdldv.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByDrivingLicenseNumber(String drivingLicenseNumber);

    // Méthodes pour dashboards
    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Long countByRoleName(@Param("roleName") Role.ERole roleName);

    default Long countByRole(String roleName) {
        try {
            Role.ERole role = Role.ERole.valueOf("ROLE_" + roleName);
            return countByRoleName(role);
        } catch (IllegalArgumentException e) {
            return 0L; // Role doesn't exist
        }
    }

    Long countByLastLoginAtAfter(LocalDateTime dateTime);

    Integer countByCreatedAtAfter(LocalDateTime dateTime);
}