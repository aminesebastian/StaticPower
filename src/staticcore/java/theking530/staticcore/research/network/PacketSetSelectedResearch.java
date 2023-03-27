package theking530.staticcore.research.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticcore.teams.Team;
import theking530.staticcore.teams.TeamManager;

public class PacketSetSelectedResearch extends NetworkMessage {
	protected String teamId;
	protected ResourceLocation researchId;

	public PacketSetSelectedResearch() {

	}

	public PacketSetSelectedResearch(String teamId, ResourceLocation researchId) {
		super();
		this.teamId = teamId;
		this.researchId = researchId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(researchId.toString());
		buffer.writeUtf(teamId.toString());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		researchId = new ResourceLocation(buffer.readUtf());
		teamId = buffer.readUtf();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Team team = TeamManager.get(ctx.get().getSender().level).getTeamById(teamId);
			if (team != null) {
				team.getResearchManager().setSelectedResearch(researchId);
			}
		});
	}
}