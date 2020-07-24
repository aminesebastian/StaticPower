package theking530.staticpower.tileentities.nonpowered.solderingtable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.PhantomSlot;
import theking530.staticpower.client.container.slots.SolderingTableOutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.items.tools.ISolderingIron;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class ContainerSolderingTable extends StaticPowerTileEntityContainer<TileEntitySolderingTable> {
	private CraftResultInventory craftResult;
	private List<ItemStack> lastCraftingPattern;

	public ContainerSolderingTable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntitySolderingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSolderingTable(int windowId, PlayerInventory playerInventory, TileEntitySolderingTable owner) {
		super(ModContainerTypes.SOLDERING_TABLE_CONTAINER, windowId, playerInventory, owner);

	}

	@Override
	public void initializeContainer() {
		lastCraftingPattern = new ArrayList<ItemStack>();
		// Craft result inventory.
		craftResult = new CraftResultInventory();
		// Add the pattern slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new PhantomSlot(getTileEntity().patternInventory, x + (y * 3), 62 + x * 18, 20 + y * 18));
				lastCraftingPattern.add(getTileEntity().patternInventory.getStackInSlot(x + (y * 3)));
			}
		}

		// Add the inventory slots.
		for (int i = 0; i < 9; i++) {
			addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, i, 8 + i * 18, 78) {
				public void onSlotChanged() {
					super.onSlotChanged();
					updateOutputSlot();
				}
			});
		}

		// Add the soldering iron slot.
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.SolderingIron), 0.3f, getTileEntity().solderingIronInventory, 0, 8, 20) {

			public void onSlotChanged() {
				super.onSlotChanged();
				updateOutputSlot();
			}
		});

		// Output slot.
		addSlot(new SolderingTableOutputSlot(this, getPlayerInventory().player, craftResult, 0, 129, 38) {
			public void onSlotChanged() {
				super.onSlotChanged();
				updateOutputSlot();
			}
		});

		// Player slots.
		addPlayerInventory(getPlayerInventory(), 8, 103);
		addPlayerHotbar(getPlayerInventory(), 8, 161);

		// Initial update of the output slot.
		updateOutputSlot();
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftingPatternChanged() {
		updateOutputSlot();
	}

	public void onItemCrafted(ItemStack output) {
		updateOutputSlot();
	}

	/**
	 * Handles checking for if the crafting pattern has changed.
	 */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int i = 0; i < 9; ++i) {
			ItemStack itemstack = this.inventorySlots.get(i).getStack();
			ItemStack itemstack1 = this.lastCraftingPattern.get(i);
			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				boolean clientStackChanged = !itemstack1.equals(itemstack, true);
				itemstack1 = itemstack.copy();
				this.lastCraftingPattern.set(i, itemstack1);

				if (clientStackChanged) {
					onCraftingPatternChanged();
				}
			}
		}
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		// If we clicked on the output slot, do the crafting.
		if (slotId == 19) {
			// Get the recipe. If we dont currently have a valid recipe, just return an
			// empty itemstack.
			SolderingRecipe recipe = getTileEntity().getCurrentRecipe().orElse(null);
			if (recipe == null || !getTileEntity().hasRequiredItems()) {
				return ItemStack.EMPTY;
			}

			if (clickTypeIn == ClickType.PICKUP) {
				// If the player clicked on the output and their held item does not stack with
				// the output, do nothing and return an empty itemstack.
				ItemStack heldItem = getPlayerInventory().getItemStack();
				if (!heldItem.isEmpty() && !ItemUtilities.areItemStacksStackable(heldItem, recipe.getRecipeOutput())) {
					return ItemStack.EMPTY;
				}

				// If crafting the item would result in a stack larger than the max stack size,
				// do nothing.
				if (recipe.getRecipeOutput().getCount() + heldItem.getCount() > recipe.getRecipeOutput().getMaxStackSize()) {
					return ItemStack.EMPTY;
				}

				// Craft the output.
				ItemStack craftedResult = getTileEntity().craftItem();

				// Update the player's held item.
				if (getPlayerInventory().getItemStack().isEmpty()) {
					getPlayerInventory().setItemStack(craftedResult);
				} else {
					getPlayerInventory().getItemStack().grow(craftedResult.getCount());
				}

				// If on the server, update the held item.
				if (!getTileEntity().getWorld().isRemote) {
					((ServerPlayerEntity) player).updateHeldItem();
				}

				// Update the output slot.
				updateOutputSlot();
				return craftedResult;
			} else if (clickTypeIn == ClickType.QUICK_MOVE) {
				if (InventoryUtilities.canFullyInsertItemIntoPlayerInventory(recipe.getRecipeOutput().copy(), player.inventory)) {
					// Craft the output.
					if (!getTileEntity().getWorld().isRemote) {
						ItemStack craftedResult = getTileEntity().craftItem();
						player.addItemStackToInventory(craftedResult);
						((ServerPlayerEntity) player).sendAllContents(this, this.getInventory());
					}
				}
				return ItemStack.EMPTY;
			} else {
				return ItemStack.EMPTY;
			}
		} else {
			return super.slotClick(slotId, dragType, clickTypeIn, player);
		}
	}

	/**
	 * Update the crafting output slot's contents.
	 * 
	 * @param slotIndex
	 * @param world
	 * @param player
	 * @param craftingInv
	 * @param outputInv
	 */
	protected void updateOutputSlot() {
		if (!getPlayerInventory().player.world.isRemote) {
			ItemStack output = ItemStack.EMPTY;
			// Set the slot contents on the server.
			if (getTileEntity().hasRequiredItems()) {
				output = getTileEntity().getCurrentRecipe().get().getRecipeOutput().copy();
			}

			craftResult.setInventorySlotContents(0, output);

			// Sync the slot.
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) getPlayerInventory().player;
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(windowId, 19, output));
		}
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		// Get the slot and the slot's contents.
		Slot slot = inventorySlots.get(slotIndex);
		ItemStack stack = slot.getStack();

		// If this is a soldering iron, place it in the soldering iron slot.
		if (stack.getItem() instanceof ISolderingIron && !mergeItemStack(stack, 18)) {
			return stack;
		}

		// If we shift clicked an item in the soldering table inventory, move it to the
		// player inventory. If we shift clicked in the player inventory, attempt to
		// move the item to the soldering iron inventory.
		if (slotIndex <= 17) {
			if (!mergeItemStack(stack, 18, 48, false)) {
				return stack;
			}
		} else {
			if (!mergeItemStack(stack, 9, 18, false)) {
				return stack;
			}
		}

		return stack;
	}
}
