package theking530.staticpower.blockentities.machines.refinery.fluidinput;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.fluids.FluidInputServoComponent;
import theking530.staticcore.blockentity.components.fluids.FluidOutputServoComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.fluid.StaticPowerFluidTank;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.machines.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.blockentities.machines.refinery.controller.BlockEntityRefineryController;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryFluidInput extends BaseRefineryBlockEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryFluidInput> INPUT_TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryFluidInput>(
			"refinery_fluid_input", (type, pos, state) -> new BlockEntityRefineryFluidInput(type, pos, state),
			ModBlocks.RefineryFluidInput);

	public static final StaticPowerFluidTank EMPTY = new StaticPowerFluidTank(0);

	public final SideConfigurationComponent ioSideConfiguration;

	private final FluidInputServoComponent inputServo1;
	private final FluidOutputServoComponent outputServo1;

	private final FluidInputServoComponent inputServo2;
	private final FluidOutputServoComponent outputServo2;

	public BlockEntityRefineryFluidInput(BlockEntityTypeAllocator<BlockEntityRefineryFluidInput> type, BlockPos pos,
			BlockState state) {
		super(type, pos, state);

		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration",
				RefineryFluidInputSideConfiguration.INSTANCE));

		registerComponent(
				inputServo1 = new FluidInputServoComponent("FluidInputServo0", 100, null, MachineSideMode.Input2));
		registerComponent(
				outputServo1 = new FluidOutputServoComponent("FluidOutputServo0", 100, null, MachineSideMode.Output2));

		registerComponent(
				inputServo2 = new FluidInputServoComponent("FluidInputServo1", 100, null, MachineSideMode.Input3));
		registerComponent(
				outputServo2 = new FluidOutputServoComponent("FluidOutputServo1", 100, null, MachineSideMode.Output3));
	}

	@Override
	public void process() {

	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (hasController()) {
			return new ContainerRefineryFluidInput(windowId, inventory, this);
		}
		return null;
	}

	@Override
	public void multiblockStateChanged(MultiblockState state) {
		super.multiblockStateChanged(state);
		if (state.isWellFormed()) {
			BlockEntityRefineryController controller = getController();
			inputServo1.setTank(controller.getInputTank(0));
			outputServo1.setTank(controller.getInputTank(0));
			inputServo2.setTank(controller.getInputTank(1));
			outputServo2.setTank(controller.getInputTank(1));
		} else {
			inputServo1.setTank(null);
			outputServo1.setTank(null);
			inputServo2.setTank(null);
			outputServo2.setTank(null);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap != ForgeCapabilities.FLUID_HANDLER || side == null || !hasController()) {
			return LazyOptional.empty();
		}

		MachineSideMode mode = getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side);
		BlockEntityRefineryController controller = getController();

		if (mode == MachineSideMode.Input2 || mode == MachineSideMode.Output2) {
			return LazyOptional.of(() -> controller.getInputTank(0)).cast();
		} else if (mode == MachineSideMode.Input3 || mode == MachineSideMode.Output3) {
			return LazyOptional.of(() -> controller.getInputTank(1)).cast();
		}
		return LazyOptional.empty();
	}
}
