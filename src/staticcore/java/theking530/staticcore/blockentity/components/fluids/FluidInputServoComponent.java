package theking530.staticcore.blockentity.components.fluids;

import java.util.function.Predicate;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;

public class FluidInputServoComponent extends AbstractBlockEntityComponent {
	private int inputRate;
	private IFluidHandler owningTank;
	private Predicate<FluidStack> filter;
	private MachineSideMode inputMode;

	public FluidInputServoComponent(String name, int inputRate, IFluidHandler owningTank, MachineSideMode inputMode, Predicate<FluidStack> filter) {
		super(name);
		this.inputRate = inputRate;
		this.owningTank = owningTank;
		this.inputMode = inputMode;
		this.filter = filter;
	}

	public FluidInputServoComponent(String name, int inputRate, IFluidHandler owningTank, MachineSideMode inputMode) {
		this(name, inputRate, owningTank, inputMode, (fluid) -> true);
	}

	@Override
	public String getComponentName() {
		return super.getComponentName();
	}

	public FluidInputServoComponent setTank(IFluidHandler tank) {
		this.owningTank = tank;
		return this;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing if this component is not enabled.
		if (!isEnabled()) {
			return;
		}
		if (owningTank == null) {
			return;
		}
		if (!getBlockEntity().getLevel().isClientSide) {
			for (Direction dir : Direction.values()) {
				// If we can't input from the provided side, skip it.
				if (!canInputFromSide(dir)) {
					continue;
				}
				suckFluid(dir);
			}
		}
	}

	public void suckFluid(Direction side) {
		// Check for the tile entity on the provided side. If null, return early.
		BlockEntity te = getLevel().getBlockEntity(getPos().relative(side));
		if (te == null) {
			return;
		}

		// Attempt to get the fluid handler for that tile entity.
		te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).ifPresent(tank -> {
			// Simulate a transfer and capture the result.
			FluidStack potentialSuck = FluidUtil.tryFluidTransfer(owningTank, tank, inputRate, false);

			// Check the predicate to ensure this fluidstack is valid.
			if (!filter.test(potentialSuck)) {
				return;
			}

			// Transfer the fluid.
			FluidUtil.tryFluidTransfer(owningTank, tank, inputRate, true);
		});
	}

	public boolean canInputFromSide(Direction side) {
		if (getBlockEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getBlockEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side) == inputMode;
		}
		return true;
	}
}