package vn.sun.membermanagementsystem.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "projects")
public class Project {
    @Id
    private Long id;
}
