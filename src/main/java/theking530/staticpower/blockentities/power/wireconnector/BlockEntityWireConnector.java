package theking530.staticpower.blockentities.power.wireconnector;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.PowerStack;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityWireConnector extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityWireConnector>(
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorBasic);

	public final WirePowerCableComponent wireComponent;

	public BlockEntityWireConnector(BlockEntityTypeAllocator<BlockEntityWireConnector> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		registerComponent(wireComponent = new WirePowerCableComponent("WireComponent") {
			@Override
			public double addPower(Direction side, PowerStack power, boolean simulate) {
				// We only accept power from null sides or the opposite side that we're facing.
				if (side == null || side == BlockEntityWireConnector.this.getFacingDirection().getOpposite()) {
					return super.addPower(side, power, simulate);
				}
				return 0;
			}
		});
	}

	public boolean isConnectedTo(BlockPos location) {
		return wireComponent.isSparselyConnectedTo(location);
	}

	public SparseCableLink addConnectedConnector(BlockPos location, CompoundTag tag) {
		return wireComponent.addSparseConnection(location, tag);
	}

	public List<SparseCableLink> removeConnectedConnector(BlockPos location) {
		return wireComponent.removeSparseConnections(location);
	}

	public List<SparseCableLink> breakAllSparseConnections() {
		return wireComponent.breakAllSparseLinks();
	}
}
