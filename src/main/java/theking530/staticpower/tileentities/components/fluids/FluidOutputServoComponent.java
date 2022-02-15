package theking530.staticpower.tileentities.components.fluids;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;

public class FluidOutputServoComponent extends AbstractTileEntityComponent {
	private int inputRate;
	private IFluidHandler owningTank;
	private MachineSideMode mode;

	public FluidOutputServoComponent(String name, int inputRate, IFluidHandler owningTank, MachineSideMode mode) {
		super(name);
		this.inputRate = inputRate;
		this.owningTank = owningTank;
		this.mode = mode;
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
		if (!isEnabled()) {
			return;
		}
		if (owningTank == null) {
			return;
		}
		if (!getTileEntity().getLevel().isClientSide) {
			for (Direction dir : Direction.values()) {
				// If we can't output from the provided side, skip it.
				if (!canOutputFromSide(dir)) {
					continue;
				}
				pushFluid(dir);
			}
		}
	}

	public void pushFluid(Direction side) {
		// Check for the tile entity on the provided side. If null, return early.
		BlockEntity te = getWorld().getBlockEntity(getPos().relative(side));
		if (te == null) {
			return;
		}

		// Attempt to get the fluid handler for that tile entity.
		te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite()).ifPresent(tank -> {
			// Transfer the fluid.
			FluidUtil.tryFluidTransfer(tank, owningTank, inputRate, true);
		});
	}

	public boolean canOutputFromSide(Direction side) {
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side) == mode;
		}
		return true;
	}
}