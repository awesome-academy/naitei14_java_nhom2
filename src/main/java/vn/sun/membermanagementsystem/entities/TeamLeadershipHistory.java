package vn.sun.membermanagementsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "team_leadership_history")
public class TeamLeadershipHistory {
    @Id
    private Long id;
}
