package vn.sun.membermanagementsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "project_leadership_history")
public class ProjectLeadershipHistory {
    @Id
    private Long id;
}
