package theking530.staticpower.tileentities.nonpowered.directdropper;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.control.RedstonePulseReactorComponent;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityDirectDropper extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityDirectDropper> TYPE = new TileEntityTypeAllocator<TileEntityDirectDropper>((type) -> new TileEntityDirectDropper(),
			ModBlocks.DirectDropper);
	public static final int DROP_DELAY = 4;

	public final InventoryComponent inventory;
	public final RedstonePulseReactorComponent pulseControl;

	public TileEntityDirectDropper() {
		super(TYPE);
		registerComponent(inventory = new InventoryComponent("Inventory", 9).setShiftClickEnabled(true));
		registerComponent(pulseControl = new RedstonePulseReactorComponent("PulseControl", DROP_DELAY, this::drop).shouldControlOnState(true)
				.setProcessingGate(() -> !InventoryUtilities.isInventoryEmpty(inventory)));
	}

	protected boolean drop() {
		ItemStack itemToDrop = InventoryUtilities.getRandomItemStackFromInventory(inventory, 1, false);
		ItemEntity itemEntity = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() - 0.5, getPos().getZ() + 0.5, itemToDrop);
		itemEntity.setMotion(0, 0, 0);
		getWorld().addEntity(itemEntity);
		getWorld().playSound(null, getPos(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.BLOCKS, 0.5f, 1.0f);
		return true;
	}

	@Override
	public boolean shouldSerializeWhenBroken() {
		return false;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDirectDropper(windowId, inventory, this);
	}
}
