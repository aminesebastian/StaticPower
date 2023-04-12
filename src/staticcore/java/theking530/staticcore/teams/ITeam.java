package theking530.staticcore.teams;

import java.util.HashSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticcore.productivity.IProductionManager;
import theking530.staticcore.research.gui.ResearchManager;

public interface ITeam extends INBTSerializable<CompoundTag> {
	public void tick(Level level);

	public String getName();

	public String getId();

	public boolean isClientSide();

	public void setName(String name);

	public HashSet<String> getPlayers();

	public boolean hasPlayer(Player player);

	public void addPlayer(Player player);

	public boolean removePlayer(Player player);

	public ResearchManager getResearchManager();

	public IProductionManager<?> getProductionManager();

	public void markDirty(boolean dirty);

	public boolean isDirty();
}
