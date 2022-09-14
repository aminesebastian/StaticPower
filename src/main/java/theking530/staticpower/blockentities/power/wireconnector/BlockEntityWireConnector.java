package theking530.staticpower.blockentities.power.wireconnector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityWireConnector extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityWireConnector> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityWireConnector>(
			(allocator, pos, state) -> new BlockEntityWireConnector(allocator, pos, state), ModBlocks.WireConnectorBasic);

	public final PowerStorageComponent powerStorage;
	public final WirePowerCableComponent wireComponent;

	public BlockEntityWireConnector(BlockEntityTypeAllocator<BlockEntityWireConnector> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT);
		ioSideConfiguration.setToDefault();

		// Add the power distributor.
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier()) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				return transferPower(stack, simulate);
			}
		}.setInputCurrentTypes(CurrentType.ALTERNATING, CurrentType.DIRECT).setSideConfiguration(ioSideConfiguration));
		powerStorage.setInputVoltageRange(getTierObject().powerConfiguration.getDefaultInputVoltageRange());
		powerStorage.setCapacity(0);

		registerComponent(wireComponent = new WirePowerCableComponent("WireComponent"));
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {

		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
	}

	public boolean isConnectedTo(BlockPos location) {
		return wireComponent.isConnectedTo(location);
	}

	public void addConnectedConnector(BlockPos location) {
		wireComponent.addConnectedConnector(location);
	}

	public boolean removeConnectedConnector(BlockPos location) {
		return wireComponent.removeConnectedConnector(location);
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		return 0;
	}
}
