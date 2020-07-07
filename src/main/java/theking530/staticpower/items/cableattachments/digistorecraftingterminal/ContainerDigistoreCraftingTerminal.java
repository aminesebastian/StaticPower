package theking530.staticpower.items.cableattachments.digistorecraftingterminal;

import java.util.Optional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.client.container.slots.CraftingRecipeInputSlot;
import theking530.staticpower.client.container.slots.DigistoreCraftingOutputSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;
import theking530.staticpower.items.cableattachments.digistoreterminal.AbstractContainerDigistoreTerminal;
import theking530.staticpower.utilities.WorldUtilities;

public class ContainerDigistoreCraftingTerminal extends AbstractContainerDigistoreTerminal<DigistoreCraftingTerminal> implements IJEIReipceTransferHandler {
	private CraftingInventory craftMatrix;
	private CraftResultInventory craftResult;

	public ContainerDigistoreCraftingTerminal(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreCraftingTerminal(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(ModContainerTypes.DIGISTORE_CRAFTING_TERMINAL, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		craftMatrix = new CraftingInventory(this, 3, 3);
		craftResult = new CraftResultInventory();

		// Limit the view to only show 5 rows to make room for the crafting GUI.
		setMaxRows(5);

		// Add the crafting input slot.
		addSlotsInGrid(craftMatrix, 0, 89, 120, 3, (index, xPos, yPos) -> new CraftingRecipeInputSlot(craftMatrix, index, xPos, yPos));

		// Add crafting output slot.
		addSlot(new DigistoreCraftingOutputSlot(this, getPlayerInventory().player, craftMatrix, craftResult, 0, 148, 138));
		super.initializeContainer();
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		// Check if the slot we clicked on is the output crafting slot and is has an
		// item
		if (inventorySlots.get(slotIndex).inventory == craftResult && !inventorySlots.get(slotIndex).getStack().isEmpty()) {
			// Then, make sure we're on the server.
			if (!getCableComponent().getWorld().isRemote) {
				// Get the digistore module.
				getDigistoreNetwork().ifPresent(digistoreModule -> {
					// Get what the output item is
					ItemStack outputItem = inventorySlots.get(slotIndex).getStack();

					// At maximum, we can only craft a stack at a time.
					int maxOutput = outputItem.getMaxStackSize();
					int crafted = 0;

					// Craft up to the max stack size.
					while (!outputItem.isEmpty() && crafted + outputItem.getCount() <= maxOutput) {
						// Perform the craft.
						inventorySlots.get(slotIndex).onTake(player, outputItem);
						crafted += outputItem.getCount();

						// Attempt the insert into the player's inventory.
						if (!player.inventory.addItemStackToInventory(outputItem)) {
							// If we weren't able to fully insert, attempt to insert the rest into the
							// digistore network before stopping early.
							ItemStack remaining = digistoreModule.insertItem(outputItem, false);
							// Last resort, if we were unable to insert the rest into the digistore network,
							// drop it in the world.
							if (!remaining.isEmpty()) {
								WorldUtilities.dropItem(getCableComponent().getWorld(), getCableComponent().getPos(), remaining);
							}
							// Stop crafting.
							return;
						}
						outputItem = inventorySlots.get(slotIndex).getStack();
					}
				});
			}
			return ItemStack.EMPTY;
		}
		return super.transferStackInSlot(player, slotIndex);
	}

	@Override
	public void consumeJEITransferRecipe(ItemStack[][] recipe) {
		clearCraftingSlots();
		getDigistoreNetwork().ifPresent(digistoreModule -> {
			for (int i = 0; i < recipe.length; i++) {
				ItemStack[] options = recipe[i];

				// Skip holes in the recipe.
				if (options == null) {
					continue;
				}
				// Loop through all the options for the slot.
				for (ItemStack item : options) {

					// First see if the item exists in the player's inventory. If it does, use that.
					int playerItemSlot = getPlayerInventory().getSlotFor(item);
					if (playerItemSlot != -1) {
						ItemStack playerExtracted = getPlayerInventory().decrStackSize(playerItemSlot, 1);
						craftMatrix.setInventorySlotContents(i, playerExtracted);
						break;
					}

					// If we didnt break from the player check, see if we can get the item from the
					// digistore network.
					ItemStack extracted = digistoreModule.extractItem(item, 1, false);
					if (!extracted.isEmpty()) {
						craftMatrix.setInventorySlotContents(i, extracted);
						break;
					}
				}
			}
		});
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		// Update the output slot.
		updateOutputSlot(this.windowId, getPlayerInventory().player.world, getPlayerInventory().player, this.craftMatrix, this.craftResult);
		resyncInv = true;
	}

	public void clearCraftingSlots() {
		// Clear the crafting slots back into the network. Do this part only on the
		// server. The client should just visually clear the slots.
		if (!getCableComponent().getWorld().isRemote) {
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
					// Skip empty slots.
					if (craftMatrix.getStackInSlot(i).isEmpty()) {
						continue;
					}
					// Attempt to insert the item.
					ItemStack output = digistoreModule.insertItem(craftMatrix.getStackInSlot(i), false);

					// If we were unable to insert the item back to the network, drop it on the
					// floor.
					if (!output.isEmpty()) {
						WorldUtilities.dropItem(getCableComponent().getWorld(), getCableComponent().getPos(), output);
					}
				}
			});
		}

		// Clear the matrix and the output slot.
		craftMatrix.clear();
		craftResult.clear();
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		clearCraftingSlots();
	}

	public void onItemCrafted(ItemStack output) {

	}

	/**
	 * Ensure we dont accidentally craft when someone double clicks the same kind of
	 * item.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}

	protected static void updateOutputSlot(int slotIndex, World world, PlayerEntity player, CraftingInventory craftingInv, CraftResultInventory outputInv) {
		if (!world.isRemote) {
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInv, world);
			if (optional.isPresent()) {
				ICraftingRecipe icraftingrecipe = optional.get();
				if (outputInv.canUseRecipe(world, serverplayerentity, icraftingrecipe)) {
					itemstack = icraftingrecipe.getCraftingResult(craftingInv);
				}
			}

			outputInv.setInventorySlotContents(0, itemstack);
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(slotIndex, 0, itemstack));
		}
	}
}
