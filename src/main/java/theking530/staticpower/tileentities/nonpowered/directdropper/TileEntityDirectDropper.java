package theking530.staticpower.tileentities.nonpowered.directdropper;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import theking530.staticcore.initialization.tileentity.TileEntityTypeAllocator;
import theking530.staticcore.initialization.tileentity.TileEntityTypePopulator;
import theking530.staticpower.blocks.tileentity.StaticPowerMachineBlock;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.tileentities.TileEntityConfigurable;
import theking530.staticpower.tileentities.components.items.InventoryComponent;
import theking530.staticpower.tileentities.components.serialization.SaveSerialize;
import theking530.staticpower.utilities.InventoryUtilities;

public class TileEntityDirectDropper extends TileEntityConfigurable {
	@TileEntityTypePopulator()
	public static final TileEntityTypeAllocator<TileEntityDirectDropper> TYPE = new TileEntityTypeAllocator<TileEntityDirectDropper>((type) -> new TileEntityDirectDropper(),
			ModBlocks.DirectDropper);
	public static final int DROP_DELAY = 8;

	public final InventoryComponent inventory;

	@SaveSerialize
	private int dropTimer;
	@SaveSerialize
	private boolean isDropping;
	@SaveSerialize
	private boolean lastRedstoneWasHigh;

	public TileEntityDirectDropper() {
		super(TYPE);
		registerComponent(inventory = new InventoryComponent("Inventory", 9).setShiftClickEnabled(true));
	}

	@Override
	public void process() {
		// Only operate on the server.
		if (world.isRemote) {
			return;
		}

		// If the inventory is empty, cancel everything and return.
		if (InventoryUtilities.isInventoryEmpty(inventory) && isDropping) {
			clearDroppingState();
			return;
		}

		// If there is a redstone signal, set isDropping to true (no need to check if it
		// already is).
		if (world.getRedstonePowerFromNeighbors(getPos()) == 15) {
			if (!lastRedstoneWasHigh) {
				initiateDroping();
				lastRedstoneWasHigh = true;
			}
		} else {
			lastRedstoneWasHigh = false;
		}

		// If we are dropping, clock up to the delay. If the drop timer elapsed, then
		// drop the item and reset the drop flags.z
		if (isDropping) {
			if (dropTimer < DROP_DELAY) {
				dropTimer++;
			} else {
				ItemStack itemToDrop = InventoryUtilities.getRandomItemStackFromInventory(inventory, 1, false);
				ItemEntity itemEntity = new ItemEntity(getWorld(), getPos().getX() + 0.5, getPos().getY() - 0.5, getPos().getZ() + 0.5, itemToDrop);
				itemEntity.setMotion(0, 0, 0);
				getWorld().addEntity(itemEntity);
				clearDroppingState();
			}
		}
	}

	protected void initiateDroping() {
		isDropping = true;
		dropTimer = 0;
		BlockState currentState = getWorld().getBlockState(getPos());
		if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
			if (currentState.get(StaticPowerMachineBlock.IS_ON) != true) {
				getWorld().setBlockState(getPos(), currentState.with(StaticPowerMachineBlock.IS_ON, true), 2);
			}
		}
	}

	protected void clearDroppingState() {
		isDropping = false;
		dropTimer = 0;
		BlockState currentState = getWorld().getBlockState(getPos());
		if (currentState.hasProperty(StaticPowerMachineBlock.IS_ON)) {
			if (currentState.get(StaticPowerMachineBlock.IS_ON) != false) {
				getWorld().setBlockState(getPos(), currentState.with(StaticPowerMachineBlock.IS_ON, false), 2);
			}
		}
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player) {
		return new ContainerDirectDropper(windowId, inventory, this);
	}
}
