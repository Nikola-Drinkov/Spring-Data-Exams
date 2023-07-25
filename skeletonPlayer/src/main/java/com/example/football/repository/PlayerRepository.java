package com.example.football.repository;

import com.example.football.models.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//ToDo:
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findFirstByEmail(String email);
    List<Player> findAllByBirthDateBetweenOrderByStatShootingDescStatPassingDescStatEnduranceDescLastNameAsc(LocalDate lower, LocalDate upper);
}
