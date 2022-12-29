package theking530.staticpower.blockentities.power.inverter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityInverter extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityInverter> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityInverter>("inverted_basic",
			(allocator, pos, state) -> new BlockEntityInverter(allocator, pos, state), ModBlocks.InverterBasic);

	public final PowerStorageComponent powerStorage;
	protected final PowerDistributionComponent powerDistributor;

	public BlockEntityInverter(BlockEntityTypeAllocator<BlockEntityInverter> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				return transferPower(stack, simulate);
			}
		}.setOutputCurrentType(CurrentType.ALTERNATING).setSideConfiguration(ioSideConfiguration));
		powerStorage.setInputVoltageRange(getTierObject().powerConfiguration.getDefaultInputVoltageRange());
		powerStorage.setCapacity(0);
	}

	@Override
	public void process() {
		if (!getLevel().isClientSide()) {

		}
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (stack.getCurrentType() == CurrentType.DIRECT) {
			double multiplier = StaticPowerEnergyUtilities.getAlternatingCurrentMultiplier(getLevel());
			PowerStack alternatingVersion = new PowerStack(stack.getPower(), stack.getVoltage() * multiplier, CurrentType.ALTERNATING);
			return powerDistributor.manuallyDistributePower(powerStorage, alternatingVersion, simulate);
		}
		return 0;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerInverter(windowId, inventory, this);
	}
}
