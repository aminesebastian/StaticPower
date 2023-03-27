package theking530.staticcore.blockentity.components.fluids;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.api.fluid.CapabilityStaticFluid;
import theking530.api.fluid.IStaticPowerFluidHandler;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.MachineSideMode;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;

public class FluidOutputServoComponent extends AbstractBlockEntityComponent {
	private int outputRate;
	private int outputPressure;
	private IFluidHandler owningTank;
	private MachineSideMode mode;

	public FluidOutputServoComponent(String name, int inputRate, IFluidHandler owningTank, MachineSideMode mode) {
		super(name);
		this.outputRate = inputRate;
		this.owningTank = owningTank;
		this.mode = mode;
		this.outputPressure = 16;
	}

	@Override
	public String getComponentName() {
		return super.getComponentName();
	}

	public FluidOutputServoComponent setTank(IFluidHandler tank) {
		this.owningTank = tank;
		return this;
	}

	@Override
	public void preProcessUpdate() {
		// Do nothing if not enabled.
		if (!isEnabled() || owningTank == null || isClientSide()) {
			return;
		}

		for (Direction dir : Direction.values()) {
			// If we can't output from the provided side, skip it.
			if (!canOutputFromSide(dir)) {
				continue;
			}
			pushFluid(dir);
		}
	}

	public FluidOutputServoComponent setOutputPressure(int outputPressure) {
		this.outputPressure = outputPressure;
		return this;
	}

	public int getOutputPressure() {
		return outputPressure;
	}

	public void pushFluid(Direction side) {
		if (owningTank.drain(1, FluidAction.SIMULATE).isEmpty()) {
			return;
		}

		BlockPos targetPos = getPos().relative(side);

		// Check for the tile entity on the provided side. If null, return early.
		BlockEntity te = getLevel().getBlockEntity(targetPos);
		if (te == null) {
			return;
		}

		FluidStack pushed = FluidStack.EMPTY;

		// First try to push to a static fluid capability.
		IStaticPowerFluidHandler staticFluidHandler = te.getCapability(CapabilityStaticFluid.STATIC_FLUID_CAPABILITY, side.getOpposite()).orElse(null);
		if (staticFluidHandler != null) {
			FluidStack simulatedDrain = owningTank.drain(outputRate, FluidAction.SIMULATE);
			int filled = staticFluidHandler.fill(simulatedDrain, outputPressure, FluidAction.EXECUTE);
			pushed = owningTank.drain(filled, FluidAction.EXECUTE);
			return;
		}

		// If the above failed, try to push to a regular fluid handler.
		IFluidHandler fluidHandler = te.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getOpposite()).orElse(null);
		if (fluidHandler != null) {
			pushed = FluidUtil.tryFluidTransfer(fluidHandler, owningTank, outputRate, true);
		}

		// Raise the callback.
		if (!pushed.isEmpty()) {
			fluidPushed(pushed, side, te);
		}
	}

	protected void fluidPushed(FluidStack fluid, Direction outputSide, BlockEntity target) {

	}

	public boolean canOutputFromSide(Direction side) {
		if (getBlockEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getBlockEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side) == mode;
		}
		return true;
	}
}