package theking530.staticpower.cables.attachments.digistore.craftingterminal;

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
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal.TerminalViewType;
import theking530.staticpower.container.slots.CraftingRecipeInputSlot;
import theking530.staticpower.container.slots.DigistoreCraftingOutputSlot;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;
import theking530.staticpower.utilities.WorldUtilities;

public class ContainerDigistoreCraftingTerminal extends AbstractContainerDigistoreTerminal<DigistoreCraftingTerminal> implements IJEIReipceTransferHandler {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreCraftingTerminal, GuiDigistoreCraftingTerminal> TYPE = new ContainerTypeAllocator<>("digistore_crafting_terminal",
			ContainerDigistoreCraftingTerminal::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreCraftingTerminal::new);
		}
	}

	private CraftingInventory craftMatrix;
	private CraftResultInventory craftResult;

	public ContainerDigistoreCraftingTerminal(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreCraftingTerminal(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		craftMatrix = new CraftingInventory(this, 3, 3);
		craftResult = new CraftResultInventory();

		// Limit the view to only show 5 rows to make room for the crafting GUI.
		setMaxRows(5);

		// Add the crafting input slot.
		addSlotsInGrid(craftMatrix, 0, 89, 120, 3, (index, xPos, yPos) -> new CraftingRecipeInputSlot(craftMatrix, index, xPos, yPos) {
			@Override
			public boolean isEnabled() {
				return getViewType() == TerminalViewType.ITEMS;
			}

		});

		// Add crafting output slot.
		addSlot(new DigistoreCraftingOutputSlot(this, getPlayerInventory().player, craftMatrix, craftResult, 0, 148, 138) {
			@Override
			public boolean isEnabled() {
				return getViewType() == TerminalViewType.ITEMS;
			}
		});

		super.initializeContainer();
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		// Check if the slot we clicked on is the output crafting slot and is has an
		// item
		if (inventorySlots.get(slotIndex).inventory == craftResult && !inventorySlots.get(slotIndex).getStack().isEmpty()) {
			// Then, make sure we're on the server.
			if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
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

		// Go to the super and cache the result.
		ItemStack output = super.transferStackInSlot(player, slotIndex);

		// If the modified slot was in the crafting matrix, notify that the matrix may
		// have changed.
		if (inventorySlots.get(slotIndex) instanceof CraftingRecipeInputSlot) {
			this.onCraftMatrixChanged(inventorySlots.get(slotIndex).inventory);
		}

		// Return the cached output.
		return output;
	}

	@Override
	public void consumeJEITransferRecipe(ItemStack[][] recipe) {
		clearCraftingSlots();
		if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
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
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		if (!getCableComponent().getWorld().isRemote && getCableComponent().isManagerPresent()) {
			// Update the output slot.
			updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, this.craftMatrix, this.craftResult);
			markForResync();
		}
	}

	@Override
	protected void onManagerStateChanged(boolean isPresent) {
		if (!isPresent) {
			clearCraftingSlots();
		}
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

					// If there is a manager, attempt to put the crafting grid contents back into
					// the system. Otherwise, drop them on the floor.
					if (digistoreModule.isManagerPresent()) {
						// Attempt to insert the item.
						ItemStack output = digistoreModule.insertItem(craftMatrix.getStackInSlot(i), false);

						// If we were unable to insert the item back to the network, drop it on the
						// floor.
						if (!output.isEmpty()) {
							WorldUtilities.dropItem(getCableComponent().getWorld(), getCableComponent().getPos(), output);
						}
					} else {
						WorldUtilities.dropItem(getCableComponent().getWorld(), getCableComponent().getPos(), craftMatrix.getStackInSlot(i));

					}
				}
			});
		}

		// Clear the matrix and the output slot.
		craftMatrix.clear();
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
	 * Ensure we don't accidentally craft when someone double clicks the same kind
	 * of item.
	 */
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
		return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
	}

	/**
	 * Update the crafting output slot's contents.
	 * 
	 * @param slotIndex
	 * 
	 * @param world
	 * 
	 * @param player
	 * 
	 * @param craftingInv
	 * 
	 * @param outputInv
	 */
	protected void updateOutputSlot(World world, PlayerEntity player, CraftingInventory craftingInv, CraftResultInventory outputInv) {
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
		}
	}
}
