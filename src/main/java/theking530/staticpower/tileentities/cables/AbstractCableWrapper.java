package theking530.staticpower.tileentities.cables;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.network.CableNetwork;

public abstract class AbstractCableWrapper {
	public enum CableAttachmentType {
		NONE, CABLE, TILE_ENTITY
	}

	protected CableAttachmentType[] Attachments;
	protected CableNetwork Network;
	protected final World World;
	protected final CableType Type;
	private final BlockPos Position;

	public AbstractCableWrapper(World world, BlockPos position, CableType type) {
		Position = position;
		World = world;
		Attachments = new CableAttachmentType[6];
		Type = type;
	}

	public BlockPos getPos() {
		return Position;
	}

	public CableNetwork getNetwork() {
		return Network;
	}

	public World getWorld() {
		return World;
	}

	public void onNetworkJoined(CableNetwork network) {
		Network = network;
		updateCableBlock();
	}

	public void onNetworkLeft() {
		Network = null;
		updateCableBlock();
	}

	public void updateCableBlock() {
		BlockState state = World.getBlockState(Position);
		World.notifyBlockUpdate(Position, state, state, 1 | 2);
	}

	public abstract boolean isAttachedOnSide(Direction direction);

	public abstract boolean isConnectedToCableOnSide(Direction direction);

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.putLong("position", Position.toLong());
		tag.putInt("type", Type.ordinal());
		return tag;
	}
}
