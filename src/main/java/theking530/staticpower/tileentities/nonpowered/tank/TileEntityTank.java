package theking530.staticpower.tileentities.nonpowered.tank;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.control.SideConfigurationComponent;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent;
import theking530.staticpower.tileentities.components.fluids.FluidInputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidOutputServoComponent;
import theking530.staticpower.tileentities.components.fluids.FluidTankComponent;
import theking530.staticpower.tileentities.components.fluids.FluidContainerComponent.FluidContainerInteractionMode;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityTank extends TileEntityBase implements INamedContainerProvider {
	public final InventoryComponent fluidContainerInventory;
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

		// Add the inventory for the fluid containers, alongside the two components for
		// filling and draining.
		registerComponent(fluidContainerInventory = new InventoryComponent("FluidContainerInventory", 4, MachineSideMode.Never));
		registerComponent(new FluidContainerComponent("FluidFillContainerServo", fluidTankComponent, fluidContainerInventory, 0, 1));
		registerComponent(new FluidContainerComponent("FluidDrainContainerServo", fluidTankComponent, fluidContainerInventory, 2, 3).setMode(FluidContainerInteractionMode.FILL));

		// Add the two components to auto input and output fluids.
		registerComponent(new FluidInputServoComponent("FluidInputServoComponent", 100, fluidTankComponent, MachineSideMode.Input));
		registerComponent(new FluidOutputServoComponent("FluidOutputServoComponent", 100, fluidTankComponent, MachineSideMode.Output));
	}

	@Override
	public void process() {
		//markTileEntityForSynchronization(); // Need to improve this.
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerTank(windowId, inventory, this);
	}
}
