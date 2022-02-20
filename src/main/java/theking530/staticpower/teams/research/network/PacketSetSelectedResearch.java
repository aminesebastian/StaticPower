package theking530.staticpower.teams.research.network;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.teams.TeamManager;

public class PacketSetSelectedResearch extends NetworkMessage {
	protected UUID teamId;
	protected ResourceLocation researchId;

	public PacketSetSelectedResearch() {

	}

	public PacketSetSelectedResearch(UUID teamId, ResourceLocation researchId) {
		super();
		this.teamId = teamId;
		this.researchId = researchId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(researchId.toString());
		buffer.writeUUID(teamId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		researchId = new ResourceLocation(buffer.readUtf());
		teamId = buffer.readUUID();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			TeamManager.get().getTeamById(teamId).ifPresent((team) -> {
				team.getResearchManager().setSelectedResearch(researchId);
			});
		});
	}
}