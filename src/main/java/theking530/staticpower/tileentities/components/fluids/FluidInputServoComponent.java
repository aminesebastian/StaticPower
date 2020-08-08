package theking530.staticpower.tileentities.components.fluids;

import java.util.function.Predicate;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import theking530.staticpower.tileentities.components.AbstractTileEntityComponent;
import theking530.staticpower.tileentities.components.control.SideConfigurationComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class FluidInputServoComponent extends AbstractTileEntityComponent {
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
		return "Fluid Input Servo";
	}

	@Override
	public void preProcessUpdate() {
		if (!getTileEntity().getWorld().isRemote) {
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
		TileEntity te = getWorld().getTileEntity(getPos().offset(side));
		if (te == null) {
			return;
		}

		// Attempt to get the fluid handler for that tile entity.
		te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).ifPresent(tank -> {
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
		if (getTileEntity().hasComponentOfType(SideConfigurationComponent.class)) {
			return getTileEntity().getComponent(SideConfigurationComponent.class).getWorldSpaceDirectionConfiguration(side) == inputMode;
		}
		return true;
	}
}