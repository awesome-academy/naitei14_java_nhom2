package vn.sun.membermanagementsystem.services;

import vn.sun.membermanagementsystem.dto.response.TeamDTO; // Import DTO
import vn.sun.membermanagementsystem.entities.Team;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    List<TeamDTO> getAllTeams();

    Optional<TeamDTO> getTeamById(Long id);

    Optional<Team> getTeamEntityById(Long id);
}