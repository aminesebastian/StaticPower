package theking530.staticpower.tileentities.cables;

import java.util.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import theking530.staticpower.tileentities.network.CableNetwork;

public abstract class AbstractCableWrapper {
	public enum CableConnectionState {
		NONE, DISABLED, CABLE, TILE_ENTITY
	}

	protected CableNetwork Network;
	protected final World World;
	protected final ResourceLocation Type;
	private final BlockPos Position;

	public AbstractCableWrapper(World world, BlockPos position, ResourceLocation type) {
		Position = position;
		World = world;
		Type = type;
	}

	public void tick() {
		if (World.getTileEntity(Position) == null) {
			System.out.println("HOW"); // Replace with log/auto repair later.
		}
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

	public ResourceLocation getType() {
		return Type;
	}

	public void onNetworkLeft() {
		Network = null;
		updateCableBlock();
	}

	public void updateCableBlock() {
		BlockState state = World.getBlockState(Position);
		World.notifyBlockUpdate(Position, state, state, 1 | 2);
	}

	public abstract CableConnectionState getSideAttachmentType(Direction direction);

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		tag.putLong("position", Position.toLong());
		return tag;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		AbstractCableWrapper cable = (AbstractCableWrapper) other;
		return World.equals(cable.World) && Position.equals(cable.Position);
	}

	@Override
	public int hashCode() {
		return Objects.hash(World, Position);
	}
}
