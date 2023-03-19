package theking530.staticpower.blockentities.digistorenetwork.patternstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.items.InventoryComponent;
import theking530.staticcore.blockentity.components.items.ItemStackHandlerFilter;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.digistorenetwork.BlockEntityDigistoreBase;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.DigistorePatternCard;

public class BlockEntityPatternStorage extends BlockEntityDigistoreBase {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<BlockEntityPatternStorage> TYPE = new BlockEntityTypeAllocator<>("digistore_pattern_storage",
			(type, pos, state) -> new BlockEntityPatternStorage(pos, state), ModBlocks.DigistorePatternStorage);

	public final InventoryComponent patternInventory;

	public BlockEntityPatternStorage(BlockPos pos, BlockState state) {
		super(TYPE, pos, state, 10);

		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 18).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				return DigistorePatternCard.hasPattern(stack);
			}
		}));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
		return new ContainerPatternStorage(windowId, inventory, this);
	}

}
