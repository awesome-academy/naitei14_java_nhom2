package vn.sun.membermanagementsystem.services.impls;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.sun.membermanagementsystem.entities.Project;
import vn.sun.membermanagementsystem.entities.Team;
import vn.sun.membermanagementsystem.repositories.ProjectRepository;
import vn.sun.membermanagementsystem.services.ProjectService;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;

    @Override
    public Page<Project> getAll(Pageable pageable) {
        return projectRepo.findAll(pageable);
    }

    @Override
    public Page<Project> getByTeam(Team team, Pageable pageable) {
        return projectRepo.findByTeam(team, pageable);
    }
}

