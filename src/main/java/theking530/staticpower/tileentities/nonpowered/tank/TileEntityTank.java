package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.sideconfiguration.MachineSideMode;
import theking530.staticpower.tileentities.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent;
import theking530.staticpower.tileentities.components.items.FluidContainerInventoryComponent.FluidContainerInteractionMode;

public class TileEntityTank extends TileEntityBase implements INamedContainerProvider {
	public final FluidContainerInventoryComponent inputFluidContainerComponent;
	public final FluidContainerInventoryComponent outputFluidContainerComponent;
	public final FluidTankComponent fluidTankComponent;
	public final SideConfigurationComponent ioSideConfiguration;

	public TileEntityTank() {
		super(ModTileEntityTypes.TANK);
		// Add the tank component.
		registerComponent(fluidTankComponent = new FluidTankComponent("FluidTank", 16000).setCapabilityExposedModes(MachineSideMode.Regular, MachineSideMode.Input, MachineSideMode.Output));
		fluidTankComponent.setCanFill(true);
		DisableFaceInteraction = false;
		// Add the side configuration component.
		registerComponent(ioSideConfiguration = new SideConfigurationComponent("SideConfiguration", (side, mode) -> {
		}, (side, mode) -> {
			return mode == MachineSideMode.Input || mode == MachineSideMode.Output || mode == MachineSideMode.Disabled || mode == MachineSideMode.Regular;
		}, new MachineSideMode[] { MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular, MachineSideMode.Regular }));

		// Add the inventory for the fluid containers.
		registerComponent(inputFluidContainerComponent = new FluidContainerInventoryComponent("FluidFillContainerServo", fluidTankComponent));
		registerComponent(outputFluidContainerComponent = new FluidContainerInventoryComponent("FluidDrainContainerServo", fluidTankComponent).setMode(FluidContainerInteractionMode.FILL));

		// Add the two components to auto input and output fluids.
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		// markTileEntityForSynchronization(); // Need to improve this.
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTank(windowId, inventory, this);
	}
}
