package theking530.staticpower.tileentities.digistorenetwork.manager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import theking530.staticpower.init.ModTileEntityTypes;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.components.power.EnergyStorageComponent;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	public static final int ENERGY_STORAGE = 1000;

	public final EnergyStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final BatteryInventoryComponent batteryInventory;

	public TileEntityDigistoreManager() {
		super(ModTileEntityTypes.DIGISTORE_MANAGER);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(energyStorage = new EnergyStorageComponent("MainEnergyStorage", ENERGY_STORAGE, ENERGY_STORAGE, ENERGY_STORAGE).setUpgradeInventory(upgradesInventory));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage.getStorage()));
	}

	@Override
	public void process() {
		if (energyStorage.hasEnoughPower(getPowerUsagePerTick())) {
			energyStorage.useBulkPower(getPowerUsagePerTick());
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDigistoreManager(windowId, inventory, this);
	}

	public int getPowerUsagePerTick() {
		return 10;
	}
}
