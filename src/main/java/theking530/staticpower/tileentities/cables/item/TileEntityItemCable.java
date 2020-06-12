package theking530.staticpower.tileentities.cables.item;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.TileEntityBase;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.network.factories.cables.CableTypes;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityItemCable extends TileEntityBase {
	public final InventoryComponent pipeInventory;
	private int moveTimer;

	public TileEntityItemCable() {
		super(ModTileEntityTypes.ITEM_CABLE);
		registerComponent(pipeInventory = new InventoryComponent("PipeInventory", 1, MachineSideMode.Input));
		registerComponent(new ItemCableComponent("ItemCableComponent", CableTypes.BASIC_ITEM));
		pipeInventory.setStackInSlot(0, new ItemStack(Blocks.DIRT));
	}

	@Override
	public void process() {
		if (moveTimer < 20) {
			moveTimer++;
		} else {
			moveTimer = 0;
		}
	}

	public float getItemMovePercent() {
		return (float) moveTimer / 20.0f;
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		super.serializeUpdateNbt(nbt);
		nbt.putInt("move_timer", moveTimer);
		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt) {
		super.deserializeUpdateNbt(nbt);
		moveTimer = nbt.getInt("move_timer");
	}
}
