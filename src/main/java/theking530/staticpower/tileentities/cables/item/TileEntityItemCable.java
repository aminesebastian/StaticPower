package theking530.staticpower.tileentities.cables.item;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.initialization.ModTileEntityTypes;
import theking530.staticpower.tileentities.cables.AbstractCableTileEntity;
import theking530.staticpower.tileentities.components.InventoryComponent;
import theking530.staticpower.tileentities.utilities.MachineSideMode;

public class TileEntityItemCable extends AbstractCableTileEntity {
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
	public boolean isValidDestinationForNetwork(TileEntity tileEntity, Direction dir) {
		if (tileEntity == null) {
			return false;
		}
		// If the tile entity is another item cable, don't consider supplying it with
		// power.
		if (tileEntity instanceof TileEntityItemCable) {
			return false;
		}

		// Get the inventory storage. If it is not present, return false.
		LazyOptional<IItemHandler> inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
		return inventory.isPresent();
	}

	@Override
	public boolean isValidCableForNetwork(BlockPos position, Direction dir) {
		if (world.getTileEntity(position) instanceof TileEntityItemCable) {
			return true;
		}
		return false;
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
