package vn.sun.membermanagementsystem.services;

import vn.sun.membermanagementsystem.entities.Project;
import vn.sun.membermanagementsystem.entities.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    Page<Project> getAll(Pageable pageable);

    Page<Project> getByTeam(Team team, Pageable pageable);

}

