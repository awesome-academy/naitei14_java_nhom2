package vn.sun.membermanagementsystem.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.sun.membermanagementsystem.dto.response.TeamDTO;
import vn.sun.membermanagementsystem.entities.Team;
import vn.sun.membermanagementsystem.mapper.TeamMapper; // Cần inject Mapper
import vn.sun.membermanagementsystem.repositories.TeamRepository;
import vn.sun.membermanagementsystem.services.TeamService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    public List<TeamDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teamMapper.toDTOList(teams);
    }

    @Override
    public Optional<TeamDTO> getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(teamMapper::toDTO);
    }

    @Override
    public Optional<Team> getTeamEntityById(Long id) {
        return teamRepository.findById(id);
    }
}