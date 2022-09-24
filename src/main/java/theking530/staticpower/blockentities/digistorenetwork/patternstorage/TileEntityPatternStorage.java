package theking530.staticpower.blockentities.digistorenetwork.patternstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.initialization.blockentity.BlockEntityTypeAllocator;
import theking530.staticcore.initialization.blockentity.BlockEntityTypePopulator;
import theking530.staticpower.blockentities.components.items.InventoryComponent;
import theking530.staticpower.blockentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.blockentities.digistorenetwork.BaseDigistoreTileEntity;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.DigistorePatternCard;

public class TileEntityPatternStorage extends BaseDigistoreTileEntity {
	@BlockEntityTypePopulator()
	public static final BlockEntityTypeAllocator<TileEntityPatternStorage> TYPE = new BlockEntityTypeAllocator<>((type, pos, state) -> new TileEntityPatternStorage(pos, state),
			ModBlocks.PatternStorage);

	public final InventoryComponent patternInventory;

	public TileEntityPatternStorage(BlockPos pos, BlockState state) {
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
