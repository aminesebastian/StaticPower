package theking530.staticcore.data.gamedata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface IStaticCoreGameData {
	public abstract void deserialize(CompoundTag tag);

	public abstract CompoundTag serialize(CompoundTag tag);

	public void tick(Level level);

	public boolean isClientSide();

	public ResourceLocation getId();

	public void onFirstTimeCreated();

	public void onSyncedFromServer(CompoundTag tag);

	public void syncToClients();
}
