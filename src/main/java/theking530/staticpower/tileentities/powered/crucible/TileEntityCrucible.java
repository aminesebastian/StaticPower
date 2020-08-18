package theking530.staticpower.tileentities.powered.crucible;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityMachine;
import theking530.staticpower.tileentities.components.control.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.InputServoComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.OutputServoComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityCrucible extends TileEntityMachine {
	public static final int DEFAULT_PROCESSING_TIME = 100;
	public static final int DEFAULT_PROCESSING_COST = 10;
	public static final int DEFAULT_MOVING_TIME = 4;

	public final InventoryComponent inputInventory;
	public final InventoryComponent internalInventory;
	public final InventoryComponent outputInventory;
	public final BatteryInventoryComponent batteryInventory;
	public final InventoryComponent upgradesInventory;

	public TileEntityCrucible() {
		super(ModTileEntityTypes.CRUCIBLE);

		registerComponent(inputInventory = new InventoryComponent("InputInventory", 1, MachineSideMode.Input));
		registerComponent(internalInventory = new InventoryComponent("InternalInventory", 1));
		registerComponent(outputInventory = new InventoryComponent("OutputInventory", 1, MachineSideMode.Output));
		registerComponent(upgradesInventory = new InventoryComponent("UpgradeInventory", 3));

		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
		registerComponent(new OutputServoComponent("OutputServo", 2, outputInventory));
		registerComponent(new InputServoComponent("InputServo", 2, inputInventory));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerCrucible(windowId, inventory, this);
	}
}
