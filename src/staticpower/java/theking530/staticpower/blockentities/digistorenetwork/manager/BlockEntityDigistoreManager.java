package theking530.staticpower.blockentities.digistorenetwork.manager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticcore.blockentity.components.items.BatteryInventoryComponent;
import theking530.staticcore.blockentity.components.items.UpgradeInventoryComponent;
import theking530.staticcore.data.StaticCoreTiers;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.TieredPowerStorageComponent;
import theking530.staticpower.blockentities.digistorenetwork.BlockEntityDigistoreBase;
import theking530.staticpower.init.ModBlocks;

public class BlockEntityDigistoreManager extends BlockEntityDigistoreBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityDigistoreManager> TYPE = new BlockEntityTypeAllocator<>("digistore_manager",
			(type, pos, state) -> new BlockEntityDigistoreManager(pos, state), ModBlocks.DigistoreManager);

	public final PowerStorageComponent energyStorage;
	public final UpgradeInventoryComponent upgradesInventory;
	public final BatteryInventoryComponent batteryInventory;

	public BlockEntityDigistoreManager(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 10);
		registerComponent(upgradesInventory = new UpgradeInventoryComponent("UpgradeInventory", 3));
		registerComponent(energyStorage = new TieredPowerStorageComponent("MainEnergyStorage", StaticCoreTiers.ENERGIZED, true, false).setUpgradeInventory(upgradesInventory));
		registerComponent(batteryInventory = new BatteryInventoryComponent("BatteryComponent", energyStorage));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerDigistoreManager(windowId, inventory, this);
	}
}
