package theking530.staticpower.blockentities.powered.refinery.fluidio;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.control.sideconfiguration.DefaultSideConfiguration;
import theking530.staticpower.blockentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;
import theking530.staticpower.blockentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.blockentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.blockentities.powered.refinery.BaseRefineryBlockEntity;
import theking530.staticpower.blockentities.powered.refinery.fluidio.input.ContainerRefineryFluidInput;
import theking530.staticpower.blockentities.powered.refinery.fluidio.output.ContainerRefineryFluidOutput;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.fluid.StaticPowerFluidTank;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityRefineryFluidIO extends BaseRefineryBlockEntity implements IFluidHandler {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryFluidIO> INPUT_TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryFluidIO>(
			(type, pos, state) -> new BlockEntityRefineryFluidIO(type, pos, state, false), ModBlocks.RefineryFluidInput);

	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityRefineryFluidIO> OUTPUT_TYPE = new BlockEntityTypeAllocator<BlockEntityRefineryFluidIO>(
			(type, pos, state) -> new BlockEntityRefineryFluidIO(type, pos, state, true), ModBlocks.RefineryFluidOutput);

	private final RefineryFluidInterface[] tankInterfaces;
	private final boolean outputMode;

	public BlockEntityRefineryFluidIO(BlockEntityTypeAllocator<BlockEntityRefineryFluidIO> type, BlockPos pos, BlockState state, boolean outputMode) {
		super(type, pos, state, StaticPowerTiers.ADVANCED);
		enableFaceInteraction();
		this.outputMode = outputMode;

		if (outputMode) {
			tankInterfaces = new RefineryFluidInterface[3];

			tankInterfaces[0] = new RefineryFluidInterface(2);
			registerComponent(new FluidOutputServoComponent("FluidOutputServo0", 100, tankInterfaces[0], MachineSideMode.Output2));

			tankInterfaces[1] = new RefineryFluidInterface(3);
			registerComponent(new FluidOutputServoComponent("FluidOutputServo1", 100, tankInterfaces[1], MachineSideMode.Output3));

			tankInterfaces[2] = new RefineryFluidInterface(4);
			registerComponent(new FluidOutputServoComponent("FluidOutputServo2", 100, tankInterfaces[2], MachineSideMode.Output4));
		} else {
			tankInterfaces = new RefineryFluidInterface[2];

			tankInterfaces[0] = new RefineryFluidInterface(0);
			// We can both input and output of input tanks.
			registerComponent(new FluidInputServoComponent("FluidInputServo0", 100, tankInterfaces[0], MachineSideMode.Input2));
			registerComponent(new FluidInputServoComponent("FluidOutputServo0", 100, tankInterfaces[0], MachineSideMode.Output2));

			tankInterfaces[1] = new RefineryFluidInterface(1);
			// We can both input and output of input tanks.
			registerComponent(new FluidInputServoComponent("FluidInputServo1", 100, tankInterfaces[1], MachineSideMode.Input3));
			registerComponent(new FluidInputServoComponent("FluidOutputServo1", 100, tankInterfaces[1], MachineSideMode.Output3));
		}

		if (outputMode) {
			ioSideConfiguration.setDefaultConfiguration(
					new DefaultSideConfiguration().setSide(BlockSide.TOP, true, MachineSideMode.Output2).setSide(BlockSide.RIGHT, true, MachineSideMode.Output3)
							.setSide(BlockSide.FRONT, true, MachineSideMode.Output4).setSide(BlockSide.BOTTOM, true, MachineSideMode.Output2)
							.setSide(BlockSide.LEFT, true, MachineSideMode.Output3).setSide(BlockSide.BACK, true, MachineSideMode.Output4));

		} else {
			ioSideConfiguration.setDefaultConfiguration(
					new DefaultSideConfiguration().setSide(BlockSide.TOP, true, MachineSideMode.Input2).setSide(BlockSide.RIGHT, true, MachineSideMode.Input2)
							.setSide(BlockSide.FRONT, true, MachineSideMode.Input2).setSide(BlockSide.BOTTOM, true, MachineSideMode.Input3)
							.setSide(BlockSide.LEFT, true, MachineSideMode.Input3).setSide(BlockSide.BACK, true, MachineSideMode.Input3));
		}
		ioSideConfiguration.setToDefault();
	}

	@Override
	public void process() {

	}

	@Override
	protected boolean isValidSideConfiguration(BlockSide side, MachineSideMode mode) {
		if (outputMode) {
			return mode == MachineSideMode.Disabled || mode == MachineSideMode.Output2 || mode == MachineSideMode.Output3 || mode == MachineSideMode.Output4;
		} else {
			return mode == MachineSideMode.Disabled || mode == MachineSideMode.Input2 || mode == MachineSideMode.Input3 || mode == MachineSideMode.Output2
					|| mode == MachineSideMode.Output3;
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		if (outputMode) {
			return new ContainerRefineryFluidOutput(windowId, inventory, this);
		} else {
			return new ContainerRefineryFluidInput(windowId, inventory, this);
		}
	}

	@Override
	public int getTanks() {
		if (hasController()) {
			getController().getTankCount();
		}
		return 0;
	}

	public IFluidTank getTank(int tank) {
		if (hasController()) {
			return getController().getTank(tank);
		}
		return StaticPowerFluidTank.EMPTY;
	}

	@Override
	public FluidStack getFluidInTank(int tank) {
		if (hasController()) {
			getController().getTank(tank).getFluid();
		}
		return FluidStack.EMPTY;
	}

	@Override
	public int getTankCapacity(int tank) {
		if (hasController()) {
			getController().getTank(tank).getCapacity();
		}
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		if (hasController()) {
			getController().getTank(tank).isFluidValid(stack);
		}
		return false;
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		if (hasController()) {
			int filled = getController().getInputTank(0).fill(resource, action);
			if (filled > 0) {
				return filled;
			}
			return getController().getInputTank(1).fill(resource, action);
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		if (hasController()) {

		}
		return FluidStack.EMPTY;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return FluidStack.EMPTY;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		// Only provide the energy capability if we are not disabled on that side.
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			if (side != null) {
				MachineSideMode mode = getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side);
				if (outputMode) {
					if (mode == MachineSideMode.Output2) {
						return LazyOptional.of(() -> tankInterfaces[0]).cast();
					} else if (mode == MachineSideMode.Output3) {
						return LazyOptional.of(() -> tankInterfaces[1]).cast();
					} else if (mode == MachineSideMode.Output4) {
						return LazyOptional.of(() -> tankInterfaces[2]).cast();
					}
				} else {
					if (mode == MachineSideMode.Input2) {
						return LazyOptional.of(() -> tankInterfaces[0]).cast();
					} else if (mode == MachineSideMode.Input3) {
						return LazyOptional.of(() -> tankInterfaces[1]).cast();
					} else if (mode == MachineSideMode.Output2) {
						return LazyOptional.of(() -> tankInterfaces[0]).cast();
					} else if (mode == MachineSideMode.Output3) {
						return LazyOptional.of(() -> tankInterfaces[1]).cast();
					}
				}
			}
		}
		return LazyOptional.empty();
	}

	public class RefineryFluidInterface implements IFluidHandler {
		private final int tankIndex;

		public RefineryFluidInterface(int tankIndex) {
			this.tankIndex = tankIndex;
		}

		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int tank) {
			if (hasController()) {
				getController().getTank(tankIndex).getFluid();
			}
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int tank) {
			if (hasController()) {
				getController().getTank(tankIndex).getCapacity();
			}
			return 0;
		}

		@Override
		public boolean isFluidValid(int tank, FluidStack stack) {
			if (hasController()) {
				getController().getTank(tankIndex).isFluidValid(stack);
			}
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (hasController() && tankIndex <= 1) {
				return getController().getTank(tankIndex).fill(resource, action);
			}
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			if (hasController() && tankIndex > 1) {
				return getController().getTank(tankIndex).drain(resource, action);
			}
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			if (hasController() && tankIndex > 1) {
				return getController().getTank(tankIndex).drain(maxDrain, action);
			}
			return FluidStack.EMPTY;
		}
	}
}
