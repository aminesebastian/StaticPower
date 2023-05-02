package theking530.api.team;

import theking530.staticcore.teams.ITeam;

public interface ITeamOwnable {

	public ITeam getOwningTeam();

	public boolean hasTeam();

	public void setTeam(String teamId);
}
