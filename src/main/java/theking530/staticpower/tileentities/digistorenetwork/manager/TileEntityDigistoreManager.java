package theking530.staticpower.tileentities.digistorenetwork.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.tileentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.components.energy.PowerStorageComponent;
import theking530.staticpower.tileentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.tileentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	@TileEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityDigistoreManager> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityDigistoreManager(pos, state),
			ModBlocks.DigistoreManager);

	public final PowerStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final BatteryInventoryComponent batteryInventory;

	public TileEntityDigistoreManager(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 10000);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(energyStorage = new PowerStorageComponent("MainEnergyStorage", StaticPowerTiers.ENERGIZED).setUpgradeInventory(upgradesInventory));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerDigistoreManager(windowId, inventory, this);
	}
}
