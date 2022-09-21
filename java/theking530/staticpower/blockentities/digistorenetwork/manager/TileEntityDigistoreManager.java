package theking530.staticpower.blockentities.digistorenetwork.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.energy.PowerStorageComponent;
import theking530.staticpower.blockentities.components.items.BatteryInventoryComponent;
import theking530.staticpower.blockentities.components.items.UpgradeInventoryComponent;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.init.ModBlocks;

public class TileEntityDigistoreManager extends BaseDigistoreTileEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityDigistoreManager> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityDigistoreManager(pos, state),
			ModBlocks.DigistoreManager);

	public final PowerStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final BatteryInventoryComponent batteryInventory;

	public TileEntityDigistoreManager(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 10);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(energyStorage = new PowerStorageComponent("MainEnergyStorage", StaticPowerTiers.ENERGIZED).setUpgradeInventory(upgradesInventory));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerDigistoreManager(windowId, inventory, this);
	}
}
