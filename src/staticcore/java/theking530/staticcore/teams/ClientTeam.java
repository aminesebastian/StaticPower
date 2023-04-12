package theking530.staticcore.teams;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import theking530.staticcore.StaticCore;
import theking530.staticcore.productivity.client.ClientProductionManager;
import theking530.staticcore.research.gui.ResearchManager;

public class ClientTeam extends AbstractTeam {
	protected final ResearchManager researchManager;
	protected final ClientProductionManager productionManager;

	public ClientTeam(String name, String id) {
		super(name, id);
		researchManager = new ResearchManager(this, true);
		productionManager = new ClientProductionManager();
	}

	public void tick(Level level) {
		level.getProfiler().push("ProductionManager.Tick");
		productionManager.tick(level.getGameTime());
		level.getProfiler().pop();
	}

	@Override
	public boolean isClientSide() {
		return true;
	}

	@Override
	public ResearchManager getResearchManager() {
		return researchManager;
	}

	@Override
	public ClientProductionManager getProductionManager() {
		return productionManager;
	}

	/**
	 * Executes the provided callback for each ServerPlayer. Calling this on the
	 * client results in a no-op.
	 * 
	 * @param callback
	 */
	protected void forEachPlayer(Consumer<ServerPlayer> callback) {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			for (String uuid : getPlayers()) {
				ServerPlayer player = (ServerPlayer) ServerLifecycleHooks.getCurrentServer().getPlayerList()
						.getPlayer(UUID.fromString(uuid));
				callback.accept(player);
			}
		} else {
			StaticCore.LOGGER.error(
					"An attempt was made to execute logic for all members on this team from the client. This is a NOOP.");
		}
	}

	public static ClientTeam fromTag(CompoundTag tag) {
		ClientTeam output = new ClientTeam(tag.getString("name"), tag.getString("id"));
		output.deserializeNBT(tag);
		return output;
	}

}