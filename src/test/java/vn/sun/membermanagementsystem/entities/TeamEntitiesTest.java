package vn.sun.membermanagementsystem.entities;

import org.junit.jupiter.api.Test;
import vn.sun.membermanagementsystem.enums.MembershipStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Team-related entities
 * Verify entity structure and basic functionality
 */
class TeamEntitiesTest {

    @Test
    void testTeamEntity() {
        Team team = new Team();
        team.setId(1L);
        team.setName("Backend Team");
        team.setDescription("Backend development team");
        team.onCreate();

        assertNotNull(team.getId());
        assertEquals("Backend Team", team.getName());
        assertEquals("Backend development team", team.getDescription());
        assertNotNull(team.getCreatedAt());
        assertNotNull(team.getUpdatedAt());
        assertNull(team.getDeletedAt());
    }

    @Test
    void testTeamMemberEntity() {
        TeamMember member = new TeamMember();
        member.setId(1L);
        member.setUserId(100L);
        member.setTeamId(200L);
        member.onCreate();

        assertNotNull(member.getId());
        assertEquals(100L, member.getUserId());
        assertEquals(200L, member.getTeamId());
        assertEquals(MembershipStatus.ACTIVE, member.getStatus());
        assertNotNull(member.getJoinedAt());
        assertNull(member.getLeftAt());
    }

    @Test
    void testTeamMemberEntityWithInactiveStatus() {
        TeamMember member = new TeamMember();
        member.setId(1L);
        member.setUserId(100L);
        member.setTeamId(200L);
        member.setStatus(MembershipStatus.INACTIVE);
        member.setJoinedAt(LocalDateTime.now().minusDays(30));
        member.setLeftAt(LocalDateTime.now().minusDays(1));

        assertEquals(MembershipStatus.INACTIVE, member.getStatus());
        assertNotNull(member.getJoinedAt());
        assertNotNull(member.getLeftAt());
    }

    @Test
    void testTeamLeadershipHistoryEntity() {
        TeamLeadershipHistory history = new TeamLeadershipHistory();
        history.setId(1L);
        history.setTeamId(200L);
        history.setLeaderId(100L);
        history.onCreate();

        assertNotNull(history.getId());
        assertEquals(200L, history.getTeamId());
        assertEquals(100L, history.getLeaderId());
        assertNotNull(history.getStartedAt());
        assertNull(history.getEndedAt());
    }

    @Test
    void testTeamLeadershipHistoryWithEndDate() {
        TeamLeadershipHistory history = new TeamLeadershipHistory();
        history.setId(1L);
        history.setTeamId(200L);
        history.setLeaderId(100L);
        history.setStartedAt(LocalDateTime.now().minusMonths(6));
        history.setEndedAt(LocalDateTime.now().minusMonths(1));

        assertNotNull(history.getStartedAt());
        assertNotNull(history.getEndedAt());
        assertTrue(history.getEndedAt().isAfter(history.getStartedAt()));
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();

        Team team = new Team(1L, "Test Team", "Description", now, now, null);
        assertEquals(1L, team.getId());
        assertEquals("Test Team", team.getName());

        TeamMember member = new TeamMember(1L, 100L, 200L, MembershipStatus.ACTIVE, now, null, null);
        assertEquals(MembershipStatus.ACTIVE, member.getStatus());

        TeamLeadershipHistory history = new TeamLeadershipHistory(1L, 200L, 100L, now, null, null);
        assertEquals(200L, history.getTeamId());
    }
}
