package com.gdldv.reservation.repository;

import com.gdldv.reservation.entity.DiscountRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRuleRepository extends JpaRepository<DiscountRule, Long> {

    /**
     * Find all active discount rules
     */
    List<DiscountRule> findByIsActiveTrue();

    /**
     * Find a discount rule by name
     */
    Optional<DiscountRule> findByRuleName(String ruleName);

    /**
     * Find discount rules by minimum completed rentals less than or equal to
     */
    List<DiscountRule> findByMinCompletedRentalsLessThanEqualAndIsActiveTrue(Integer completedRentals);
}
