package theking530.staticpower.blockentities.power.wireconnector;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.cablenetwork.SparseCableLink;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.data.StaticPowerTier;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityWireConnector extends BlockEntityBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityWireConnector>("wire_connector_lv",
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorLV);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_ADVANCED = new BlockEntityTypeAllocator<BlockEntityWireConnector>("wire_connector_mv",
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorMV);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_STATIC = new BlockEntityTypeAllocator<BlockEntityWireConnector>("wire_connector_hv",
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorHV);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_ENERGIZED = new BlockEntityTypeAllocator<BlockEntityWireConnector>("wire_connector_ev",
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorEV);
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_LUMUM = new BlockEntityTypeAllocator<BlockEntityWireConnector>("wire_connector_bv",
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorBV);

	public final WirePowerCableComponent wireComponent;

	public BlockEntityWireConnector(BlockEntityTypeAllocator<BlockEntityWireConnector> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		StaticPowerTier tier = getTierObject();
		StaticPowerVoltage voltage = tier.cablePowerConfiguration.wireTerminalMaxVoltage.get();
		double maxPower = tier.cablePowerConfiguration.wireTerminalMaxCurrent.get();
		double powerLoss = tier.cablePowerConfiguration.wireCoilPowerLossPerBlock.get();

		// TODO: Power loss here has to be updated per wire type, not per terminal.
		registerComponent(wireComponent = new WirePowerCableComponent("WireComponent", voltage, maxPower, powerLoss) {
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
