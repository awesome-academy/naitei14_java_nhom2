package vn.sun.membermanagementsystem.repositories;

import vn.sun.membermanagementsystem.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
