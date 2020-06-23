package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticpower.initialization.ModFluids;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.FluidTankComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityTank extends TileEntityBase implements INamedContainerProvider {

	public final FluidTankComponent fluidTankComponent;

	public TileEntityTank() {
		super(ModTileEntityTypes.TANK);
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 5000, MachineSideMode.Regular));
		fluidTankComponent.setCanFill(false);
	}

	@Override
	public void process() {
		fluidTankComponent.fill(new FluidStack(ModFluids.TreeSap.Fluid, 100), FluidAction.EXECUTE);
	}
}
