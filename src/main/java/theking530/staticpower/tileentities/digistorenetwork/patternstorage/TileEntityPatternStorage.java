package theking530.staticpower.tileentities.digistorenetwork.patternstorage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.items.DigistorePatternCard;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.items.ItemStackHandlerFilter;
import theking530.staticpower.tileentities.digistorenetwork.BaseDigistoreTileEntity;

public class TileEntityPatternStorage extends BaseDigistoreTileEntity {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityPatternStorage> TYPE = new TileEntityTypeAllocator<>((type) -> new TileEntityPatternStorage(), ModBlocks.PatternStorage);

	public final InventoryComponent patternInventory;

	public TileEntityPatternStorage() {
		super(TYPE, 10);

		registerComponent(patternInventory = new InventoryComponent("PatternInventory", 18).setShiftClickEnabled(true).setFilter(new ItemStackHandlerFilter() {
			@Override
			public boolean canInsertItem(int slot, ItemStack stack) {
				return DigistorePatternCard.hasPattern(stack);
			}
		}));
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerPatternStorage(windowId, inventory, this);
	}

}
