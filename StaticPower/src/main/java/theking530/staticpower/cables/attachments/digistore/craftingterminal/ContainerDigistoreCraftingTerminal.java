package theking530.staticpower.cables.attachments.digistore.craftingterminal;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.cablenetwork.AbstractCableProviderComponent;
import theking530.staticcore.container.slots.CraftingRecipeInputSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticcore.utilities.PlayerUtilities;
import theking530.staticcore.utilities.item.InventoryUtilities;
import theking530.staticcore.world.WorldUtilities;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractGuiDigistoreTerminal.TerminalViewType;
import theking530.staticpower.container.slots.DigistoreCraftingOutputSlot;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class ContainerDigistoreCraftingTerminal extends AbstractContainerDigistoreTerminal<DigistoreCraftingTerminal>
		implements IJEIReipceTransferHandler {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreCraftingTerminal, GuiDigistoreCraftingTerminal> TYPE = new ContainerTypeAllocator<>(
			"digistore_crafting_terminal", ContainerDigistoreCraftingTerminal::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreCraftingTerminal::new);
		}
	}

	private CraftingContainer craftMatrix;
	private ResultContainer craftResult;

	public ContainerDigistoreCraftingTerminal(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreCraftingTerminal(int windowId, Inventory playerInventory, ItemStack attachment,
			Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		craftMatrix = new CraftingContainer(this, 3, 3);
		craftResult = new ResultContainer();

		// Add the crafting input slot.
		addSlotsInGrid(craftMatrix, 0, 89, 120, 3,
				(index, xPos, yPos) -> new CraftingRecipeInputSlot(craftMatrix, index, xPos, yPos) {
					@Override
					public boolean isActive() {
						return getViewType() == TerminalViewType.ITEMS;
					}

				});

		// Add crafting output slot.
		addSlot(new DigistoreCraftingOutputSlot(this, getPlayerInventory().player, craftMatrix, craftResult, 0, 148,
				138) {
			@Override
			public boolean isActive() {
				return getViewType() == TerminalViewType.ITEMS;
			}
		});

		super.initializeContainer();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slotIndex) {
		// Check if the slot we clicked on is the output crafting slot and is has an
		// item
		if (slots.get(slotIndex).container == craftResult && !slots.get(slotIndex).getItem().isEmpty()) {
			// Then, make sure we're on the server.
			if (!getCableComponent().getLevel().isClientSide && getCableComponent().isManagerPresent()) {
				// Get the digistore module.
				getDigistoreNetwork().ifPresent(digistoreModule -> {
					// Get what the output item is
					ItemStack outputItem = slots.get(slotIndex).getItem();

					// At maximum, we can only craft a stack at a time.
					int maxOutput = outputItem.getMaxStackSize();
					int crafted = 0;

					// Craft up to the max stack size.
					while (!outputItem.isEmpty() && crafted + outputItem.getCount() <= maxOutput) {
						// Perform the craft.
						slots.get(slotIndex).onTake(player, outputItem);
						crafted += outputItem.getCount();

						// Attempt the insert into the player's inventory.
						if (!player.getInventory().add(outputItem)) {
							// If we weren't able to fully insert, attempt to insert the rest into the
							// digistore network before stopping early.
							ItemStack remaining = digistoreModule.insertItem(outputItem, false);
							// Last resort, if we were unable to insert the rest into the digistore network,
							// drop it in the world.
							if (!remaining.isEmpty()) {
								WorldUtilities.dropItem(getCableComponent().getLevel(), getCableComponent().getPos(),
										remaining);
							}
							// Stop crafting.
							return;
						}
						outputItem = slots.get(slotIndex).getItem();
					}
				});
			}
			return ItemStack.EMPTY;
		}

		// Go to the super and cache the result.
		ItemStack output = super.quickMoveStack(player, slotIndex);

		// If the modified slot was in the crafting matrix, notify that the matrix may
		// have changed.
		if (slots.get(slotIndex) instanceof CraftingRecipeInputSlot) {
			this.slotsChanged(slots.get(slotIndex).container);
		}

		// Return the cached output.
		return output;
	}

	@Override
	public void consumeJEITransferRecipe(Player playerIn, ItemStack[][] recipe) {
		clearCraftingSlots(playerIn);
		if (!getCableComponent().getLevel().isClientSide && getCableComponent().isManagerPresent()) {
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
						int playerItemSlot = PlayerUtilities
								.getSlotForStackInPlayerInventory(getPlayerInventory().player, item);
						if (playerItemSlot != -1) {
							ItemStack playerExtracted = getPlayerInventory().removeItem(playerItemSlot, 1);
							craftMatrix.setItem(i, playerExtracted);
							break;
						}

						// If we didnt break from the player check, see if we can get the item from the
						// digistore network.
						ItemStack extracted = digistoreModule.extractItem(item, 1, false);
						if (!extracted.isEmpty()) {
							craftMatrix.setItem(i, extracted);
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
	public void slotsChanged(Container inventoryIn) {
		if (!getCableComponent().getLevel().isClientSide && getCableComponent().isManagerPresent()) {
			// Update the output slot.
			updateOutputSlot(getPlayerInventory().player.level, getPlayerInventory().player, this.craftMatrix,
					this.craftResult);
			markForResync();
		}
	}

	public void clearCraftingSlots(@Nullable Player playerIn) {
		// Clear the crafting slots back into the network. Do this part only on the
		// server. The client should just visually clear the slots.
		if (!getCableComponent().getLevel().isClientSide) {
			getDigistoreNetwork().ifPresent(digistoreModule -> {
				for (int i = 0; i < craftMatrix.getContainerSize(); i++) {
					// Skip empty slots.
					if (craftMatrix.getItem(i).isEmpty()) {
						continue;
					}

					boolean haveLeftover = true;
					ItemStack leftover = ItemStack.EMPTY;
					// If there is a manager, attempt to put the crafting grid contents back into
					// the system. Otherwise, drop them on the floor.
					if (digistoreModule.isManagerPresent()) {
						// Attempt to insert the item.
						ItemStack output = digistoreModule.insertItem(craftMatrix.getItem(i), false);

						// If we were unable to insert the item back to the network, try to insert it
						// into the player. If we can't, drop it on the
						// floor.
						if (!output.isEmpty()) {
							haveLeftover = false;
						} else {
							haveLeftover = true;
							leftover = output;
						}
					} else {
						haveLeftover = true;
						leftover = craftMatrix.getItem(i);
					}

					// If we have leftover, try to insert it into the player. If the
					// player is null, drop it in the world.
					// If the player also can't take it, drop it into the world.
					if (haveLeftover) {
						if (playerIn != null) {
							if (InventoryUtilities.canPartiallyInsertItemIntoPlayerInventory(leftover,
									playerIn.getInventory())) {
								if (!playerIn.addItem(leftover)) {
									WorldUtilities.dropItem(getCableComponent().getLevel(), playerIn.blockPosition(),
											leftover);
								}
							} else {
								WorldUtilities.dropItem(getCableComponent().getLevel(),
										playerIn != null ? playerIn.blockPosition() : getCableComponent().getPos(),
										leftover);

							}
						}
					}
				}
			});
		} else {
			StaticPowerMessageHandler.MAIN_PACKET_CHANNEL
					.sendToServer(new PacketClearDigistoreCraftingTerminal(containerId));
		}

		// Clear the matrix and the output slot.
		this.craftResult.setItem(0, ItemStack.EMPTY);
		craftMatrix.clearContent();
	}

	/**
	 * Called when the container is closed.
	 */
	public void removed(Player playerIn) {
		super.removed(playerIn);
		clearCraftingSlots(playerIn);
	}

	public void onItemCrafted(ItemStack[] recipe, ItemStack output) {
		// Update the output slot.
		updateOutputSlot(getPlayerInventory().player.level, getPlayerInventory().player, this.craftMatrix,
				this.craftResult);
	}

	/**
	 * Ensure we don't accidentally craft when someone double clicks the same kind
	 * of item.
	 */
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
	}

	@Override
	protected void onManagerStateChanged(boolean isPresent) {
		if (!isPresent) {
			clearCraftingSlots(null);
		}
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
	protected void updateOutputSlot(Level world, Player player, CraftingContainer craftingInv,
			ResultContainer outputInv) {
		if (!world.isClientSide) {
			ServerPlayer serverplayerentity = (ServerPlayer) player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,
					craftingInv, world);
			if (optional.isPresent()) {
				CraftingRecipe icraftingrecipe = optional.get();
				if (outputInv.setRecipeUsed(world, serverplayerentity, icraftingrecipe)) {
					itemstack = icraftingrecipe.assemble(craftingInv);
				}
			}

			outputInv.setItem(0, itemstack);
		}
	}
}
