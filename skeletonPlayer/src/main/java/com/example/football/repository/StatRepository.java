package com.example.football.repository;

import com.example.football.models.entity.Stat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//ToDo:
@Repository
public interface StatRepository extends JpaRepository<Stat, Long> {
    Optional<Stat> findFirstByPassingAndShootingAndEndurance(Float passing, Float shooting, Float endurance);
    Optional<Stat> findFirstById(Long id);
}
