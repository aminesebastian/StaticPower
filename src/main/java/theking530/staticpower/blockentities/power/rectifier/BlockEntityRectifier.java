package theking530.staticpower.blockentities.power.rectifier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.api.energy.CurrentType;
import theking530.api.energy.PowerStack;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.StaticVoltageRange;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.BlockEntityConfigurable;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.energy.PowerDistributionComponent;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRectifier extends BlockEntityConfigurable {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRectifier> TYPE_BASIC = new BlockEntityTypeAllocator<BlockEntityRectifier>("rectifier",
			(allocator, pos, state) -> new BlockEntityRectifier(allocator, pos, state), ModBlocks.Rectifier);

	public final PowerStorageComponent powerStorage;
	protected final PowerDistributionComponent powerDistributor;

	public BlockEntityRectifier(BlockEntityTypeAllocator<BlockEntityRectifier> allocator, BlockPos pos, BlockState state) {
		super(allocator, pos, state);

		// Enable face interaction.
		enableFaceInteraction();
		ioSideConfiguration.setDefaultConfiguration(SideConfigurationComponent.FRONT_BACK_INPUT_OUTPUT, true);

		// Add the power distributor.
		registerComponent(powerDistributor = new PowerDistributionComponent("PowerDistributor"));
		registerComponent(powerStorage = new PowerStorageComponent("MainEnergyStorage", getTier(), true, true) {
			@Override
			public double addPower(PowerStack stack, boolean simulate) {
				if (!powerStorage.getInputVoltageRange().isVoltageInRange(stack.getVoltage())) {
					return super.addPower(stack, simulate);
				}
				return transferPower(stack, simulate);
			}
		}.setInputCurrentTypes(CurrentType.ALTERNATING).setOutputCurrentType(CurrentType.DIRECT).setSideConfiguration(ioSideConfiguration));
		powerStorage.setInputVoltageRange(StaticVoltageRange.ANY_VOLTAGE);
		powerStorage.setOutputVoltage(StaticPowerVoltage.ZERO);

		powerStorage.setMaximumOutputPower(Double.MAX_VALUE);
		powerStorage.setMaximumOutputPower(Double.MAX_VALUE);

		powerStorage.setCapacity(0);
	}

	@Override
	public void process() {
	}

	public double transferPower(PowerStack stack, boolean simulate) {
		if (stack.getCurrentType() == CurrentType.ALTERNATING) {
			PowerStack directVersion = new PowerStack(Math.abs(stack.getPower()), Math.abs(stack.getVoltage()), CurrentType.DIRECT);
			return powerDistributor.manuallyDistributePower(powerStorage, directVersion, simulate);
		}
		return 0;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerRectifier(windowId, inventory, this);
	}
}
