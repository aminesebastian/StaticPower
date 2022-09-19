package theking530.staticpower.blockentities.digistorenetwork.wireterminal;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.cables.SparseCableLink;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityDigitstoreWireConnector extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityDigitstoreWireConnector> TYPE = new BlockEntityTypeAllocator<BlockEntityDigitstoreWireConnector>(
			(allocator, pos, state) -> new BlockEntityDigitstoreWireConnector(allocator, pos, state), ModBlocks.WireConnectorDigistore);

	public final WireDigistoreCableProviderComponent wireComponent;

	public BlockEntityDigitstoreWireConnector(BlockEntityTypeAllocator<BlockEntityDigitstoreWireConnector> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);
		registerComponent(wireComponent = new WireDigistoreCableProviderComponent("WireComponent"));
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
