package theking530.staticpower.tileentities.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityDigistoreManager> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityDigistoreManager(), ModBlocks.DigistoreManager);

	public static final int ENERGY_STORAGE = 1000000;

	public final EnergyStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final BatteryInventoryComponent batteryInventory;

	public TileEntityDigistoreManager() {
		super(TYPE, 10000);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", ENERGY_STORAGE, ENERGY_STORAGE, ENERGY_STORAGE).setUpgradeInventory(upgradesInventory));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistoreManager(windowId, inventory, this);
	}
}
