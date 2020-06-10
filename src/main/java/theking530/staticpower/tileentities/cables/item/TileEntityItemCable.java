package theking530.staticpower.tileentities.cables.item;

import net.minecraft.nbt.CompoundNBT;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.cables.AbstractCableTileEntity;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityItemCable extends AbstractCableTileEntity<ItemCableWrapper> {
	public final InventoryComponent pipeInventory;
	private int moveTimer;

	public TileEntityItemCable() {
		super(ModTileEntityTypes.ITEM_CABLE);
		registerComponent(pipeInventory = new InventoryComponent("PipeInventory", 1, MachineSideMode.Input));
	}

	@Override
	public void process() {

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

	@Override
	public ItemCableWrapper createWrapper() {
		return new ItemCableWrapper(world, getPos());
	}
}
