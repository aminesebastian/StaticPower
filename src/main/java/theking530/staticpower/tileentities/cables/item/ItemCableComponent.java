package theking530.staticpower.tileentities.cables.item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import theking530.staticpower.items.cableattachments.BasicExtractorAttachment;
import theking530.staticpower.tileentities.cables.AbstractCableProviderComponent;
import theking530.staticpower.tileentities.cables.AbstractCableWrapper.CableConnectionState;
import theking530.staticpower.tileentities.cables.CableUtilities;
import theking530.staticpower.tileentities.cables.network.CableNetwork;
import theking530.staticpower.tileentities.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.cables.network.pathfinding.Path;
import theking530.staticpower.utilities.InventoryUtilities;

public class ItemCableComponent extends AbstractCableProviderComponent {
	private static final Logger LOGGER = LogManager.getLogger(ItemCableComponent.class);
	private ItemRoutingPacket currentPacket;
	private int moveTimer;
	private int moveTime = 20;

	private ItemStack containedItem;
	private Direction inDirection;
	private Direction outDirection;

	public ItemCableComponent(String name, ResourceLocation type) {
		super(name, type);
		containedItem = ItemStack.EMPTY;
	}

	@Override
	public void preProcessUpdate() {
		super.preProcessUpdate();
		if (currentPacket != null || !containedItem.isEmpty()) {
			if (moveTimer < moveTime) {
				moveTimer++;
			} else {
				if (!getWorld().isRemote && currentPacket != null) {
					if (handOffContainedItem()) {
						moveTimer = 0;
						currentPacket = null;
						containedItem = ItemStack.EMPTY;
						getTileEntity().markTileEntityForSynchronization();
					}
				}
			}
		} else {
			moveTimer = 0;
		}
	}

	public ItemStack getContainedItem() {
		return containedItem;
	}

	public Direction getItemAnimationDirection() {
		return moveTimer > moveTime / 2 ? outDirection : inDirection;
	}

	/**
	 * Returns the item move percent mapped to the range [-1, 1].
	 * 
	 * @return
	 */
	public float getItemMoveLerp() {
		return (getItemMovePercent() * 2.0f) - 1.0f;
	}

	public float getItemMovePercent() {
		return (float) getCurrentMoveTimer() / 20.0f;
	}

	public int getCurrentMoveTimer() {
		return moveTimer;
	}

	@Override
	protected void processAttachment(Direction side, ItemStack attachment) {
		// Do nothing if we are currently transferring an item or the attachment is not
		// an extractor attachment.
		if (currentPacket != null) {
			return;
		}

		// Process the extractor attachment.
		if (attachment.getItem() instanceof BasicExtractorAttachment) {
			processExtractorAttachment(side, attachment);
		}
	}

	protected void processExtractorAttachment(Direction side, ItemStack attachment) {

		// Do nothing if we're on the client.
		if (getWorld().isRemote) {
			return;
		}

		// Get the network.
		CableNetwork network = CableNetworkManager.get(getWorld()).getCable(getPos()).getNetwork();
		if (network == null) {
			LOGGER.error(String.format("Encountered a null network for an ItemCableComponent at position: %1$s.", getPos()));
			return;
		}

		// Preallocate the extracted item, the source, and the destination.
		int extractedSlot = -1;
		IItemHandler sourceInventory = null;
		Path path = null;

		// Attempt to pull and find a path for an item.
		if (!attachment.isEmpty() && attachment.getItem() instanceof BasicExtractorAttachment) {
			TileEntity te = getWorld().getTileEntity(getPos().offset(side));
			if (te != null) {
				sourceInventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).orElse(null);
				if (sourceInventory != null) {
					for (extractedSlot = 0; extractedSlot < sourceInventory.getSlots(); extractedSlot++) {
						ItemStack extractedItem = sourceInventory.extractItem(extractedSlot, 64, true);
						if (!extractedItem.isEmpty()) {
							path = findPathForItem(te, side, extractedItem);
							if (path != null) {
								break;
							}
						}
					}
				}
			}
		}

		// If the destinationTileEntity is not null, it indicates we found a complete
		// path for extraction.
		if (path != null) {
			extractItem(sourceInventory, extractedSlot, path);
			inDirection = side.getOpposite();
		}
	}

	protected Path findPathForItem(TileEntity sourceTileEntity, Direction sourceDir, ItemStack item) {
		TileEntity destination = getNetwork().getClosestTileEntity(getPos(), (dest, dir) -> {
			if (dest == sourceTileEntity && dir == sourceDir) {
				return false;
			}
			IItemHandler destInv = dest.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite()).orElse(null);
			return InventoryUtilities.canInsertItemIntoInventory(destInv, item);
		});

		if (destination != null) {
			return getNetwork().getPathCache().getPath(getPos(), destination.getPos());
		}
		return null;
	}

	protected void extractItem(IItemHandler sourceInventory, int extractedSlot, Path path) {
		// Get the destination inventory.
		TileEntity destinationTe = getWorld().getTileEntity(path.getDestinationLocation());
		IItemHandler destInventory = destinationTe.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, path.getDestinationDirection().getOpposite()).orElse(null);

		// Simulate the extract to see how many we can grab, and then simulate the
		// insert to see how many we can insert.
		ItemStack actualExtract = sourceInventory.extractItem(extractedSlot, 64, true);
		ItemStack canInsert = InventoryUtilities.insertItemIntoInventory(destInventory, actualExtract.copy(), true);

		// Then, perform the actual extraction and make the extraction packet. Sync the
		// data to the client.
		actualExtract = sourceInventory.extractItem(extractedSlot, actualExtract.getCount() - canInsert.getCount(), true);

		if (!actualExtract.isEmpty()) {
			currentPacket = new ItemRoutingPacket(actualExtract, path);
			containedItem = actualExtract.copy();
			outDirection = currentPacket.getNextEntry().getDirectionOfApproach();
			getTileEntity().markTileEntityForSynchronization();
		}
	}

	protected boolean handOffContainedItem() {
		BlockPos nextPosition = currentPacket.getNextEntry().getPosition();
		if (CableNetworkManager.get(getWorld()).isTrackingCable(nextPosition)) {
			AbstractCableProviderComponent nextComp = CableUtilities.getCableWrapperComponent(getWorld(), nextPosition);
			if (nextComp instanceof ItemCableComponent) {
				ItemCableComponent nextItemComp = (ItemCableComponent) nextComp;
				if (nextItemComp.acceptItem(outDirection, currentPacket)) {
					return true;
				}
			}
		} else if (getWorld().getTileEntity(nextPosition) != null) {
			TileEntity te = getWorld().getTileEntity(nextPosition);
			if (te != null) {
				IItemHandler outputInventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, currentPacket.getNextEntry().getDirectionOfApproach()).orElse(null);
				if (outputInventory != null) {
					InventoryUtilities.insertItemIntoInventory(outputInventory, currentPacket.getContainedItem(), false);
					return true;
				}
			}
		} else {
			//Path newPath = findPathForItem(null, null, currentPacket.getContainedItem());
		}
		return false;
	}

	protected boolean acceptItem(Direction fromDirection, ItemRoutingPacket itemPacket) {
		if (currentPacket == null) {
			moveTimer = 0;
			currentPacket = itemPacket;
			currentPacket.incrementCurrentPathIndex();
			containedItem = itemPacket.getContainedItem();
			inDirection = fromDirection;
			outDirection = currentPacket.getNextEntry().getDirectionOfApproach();
			getTileEntity().markTileEntityForSynchronization();
			return true;
		}
		return false;
	}

	@Override
	protected CableConnectionState cacheConnectionState(Direction side, BlockPos blockPosition) {
		AbstractCableProviderComponent overProvider = CableUtilities.getCableWrapperComponent(getWorld(), blockPosition);
		if (overProvider != null && overProvider.getCableType() == getCableType()) {
			return CableConnectionState.CABLE;
		} else if (getWorld().getTileEntity(blockPosition) != null) {
			TileEntity te = getWorld().getTileEntity(blockPosition);
			if (te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite()).isPresent()) {
				return CableConnectionState.TILE_ENTITY;
			}
		}
		return CableConnectionState.NONE;
	}

	@Override
	protected boolean canAttachAttachment(ItemStack attachment) {
		return true;
	}

	@Override
	public CompoundNBT serializeUpdateNbt(CompoundNBT nbt) {
		super.serializeUpdateNbt(nbt);

		// Serialize the contained item.
		CompoundNBT containedItemTag = new CompoundNBT();
		containedItem.write(containedItemTag);
		nbt.put("contained_item", containedItemTag);

		// Serialize the move timer.
		nbt.putInt("move_timer", moveTimer);

		if (inDirection != null) {
			nbt.putInt("in_direction", inDirection.ordinal());
		}
		if (outDirection != null) {
			nbt.putInt("out_direction", outDirection.ordinal());
		}

		return nbt;
	}

	@Override
	public void deserializeUpdateNbt(CompoundNBT nbt) {
		super.deserializeUpdateNbt(nbt);

		// Deserialize the contained item.
		CompoundNBT containedItemTag = nbt.getCompound("contained_item");
		containedItem = ItemStack.read(containedItemTag);

		// Deserialize the move timer.
		moveTimer = nbt.getInt("move_timer");
		if (nbt.contains("in_direction")) {
			inDirection = Direction.values()[nbt.getInt("in_direction")];
		}
		if (nbt.contains("out_direction")) {
			outDirection = Direction.values()[nbt.getInt("out_direction")];
		}
	}
}
