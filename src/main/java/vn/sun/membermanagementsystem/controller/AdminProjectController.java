package vn.sun.membermanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.sun.membermanagementsystem.entities.Project;
import vn.sun.membermanagementsystem.entities.Team;
import vn.sun.membermanagementsystem.repositories.TeamRepository;
import vn.sun.membermanagementsystem.services.ProjectService;

import java.util.Collections;

@Controller
@RequestMapping("/admin/projects")
@RequiredArgsConstructor
public class AdminProjectController {

    private final ProjectService projectService;
    private final TeamRepository teamRepository;

    @GetMapping
    public String listProjects(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long teamId
    ) {

        PageRequest pageable = PageRequest.of(page, 10);

        Page<Project> projects;

        if (teamId != null) {
            Team team = teamRepository.findById(teamId).orElse(null);
            projects = projectService.getByTeam(team, pageable);
        } else {
            projects = projectService.getAll(pageable);
        }

        model.addAttribute("projects", projects);
        model.addAttribute("teams", teamRepository.findAll() != null ? teamRepository.findAll() : Collections.emptyList());
        model.addAttribute("selectedTeamId", teamId);

        return "admin/projects/index";
    }
}

