package theking530.staticcore.data.gamedata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public interface IStaticCoreGameData {
	/**
	 * This is where game data should handle the deserialization of data. This could
	 * either be data coming from the gamesave or from a network packet.
	 * 
	 * @param tag
	 */
	public abstract void deserialize(CompoundTag tag);

	/**
	 * This method should return all the data that needs to be saved to the game
	 * save.
	 * 
	 * @param tag
	 * @return
	 */
	public abstract CompoundTag serialize(CompoundTag tag);

	/**
	 * This method gets called once per level's tick. To limit the tick to once per
	 * server tick, check whether this level is the overworld and only perform your
	 * logic if it is.
	 * 
	 * @param level
	 */
	public void tick(Level level);

	/**
	 * Returns whether or not this is the client side version of the data.
	 * 
	 * @return
	 */
	public boolean isClientSide();

	/**
	 * This method should return the ID of this game data instance.
	 * 
	 * @return
	 */
	public ResourceLocation getId();

	/**
	 * This method is called only when the data is created for the first time. This
	 * is where you should perform any logic that needs to be performed only once on
	 * game start.
	 */
	public void onFirstTimeCreated();

	/**
	 * This method is raised when the default sync packet is recieved from the
	 * server.
	 * 
	 * @param tag
	 */
	public void onSyncedFromServer(CompoundTag tag);

	/**
	 * Calling this method should synchronize this game data to all players. This
	 * method throws an exception if called on the client.
	 */
	public void syncToClients();

	/**
	 * Calling this method should synchronize this game data to the provided player.
	 * This method throws an exception if called on the client.
	 */
	public void syncToClient(ServerPlayer player);
}
