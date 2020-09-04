package theking530.staticpower.tileentities.nonpowered.solderingtable;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.SolderingTableOutputSlot;
import theking530.staticpower.data.crafting.wrappers.soldering.SolderingRecipe;
import theking530.staticpower.utilities.InventoryUtilities;
import theking530.staticpower.utilities.ItemUtilities;

public class ContainerSolderingTable extends AbstractContainerSolderingTable<TileEntitySolderingTable> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerSolderingTable, GuiSolderingTable> TYPE = new ContainerTypeAllocator<>("soldering_table", ContainerSolderingTable::new, GuiSolderingTable::new);

	private @Nullable SolderingTableOutputSlot outputSlot;
	private CraftResultInventory craftResult;

	public ContainerSolderingTable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntitySolderingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerSolderingTable(int windowId, PlayerInventory playerInventory, TileEntitySolderingTable owner) {
		super(TYPE, windowId, playerInventory, owner);
		enableSolderingIronSlot = true;

	}

	@Override
	public void initializeContainer() {
		enableSolderingIronSlot = true;

		super.initializeContainer();

		// Initial update of the output slot.
		updateOutputSlot();
	}

	@Override
	protected void addOutputSlot() {
		// Craft result inventory.
		craftResult = new CraftResultInventory();
		// Output slot.
		addSlot(outputSlot = new SolderingTableOutputSlot(getPlayerInventory().player, craftResult, 0, 129, 38));
	}

	/**
	 * Handles checking for if the crafting pattern has changed.
	 */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		updateOutputSlot();
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

				// Tell the slot we crafted.
				outputSlot.onCrafted(player, craftedResult);

				// Update the output slot (do this even though we do it on tick to ensure we
				// dont display an item when its no longer craftable).
				updateOutputSlot();
				return craftedResult;
			} else if (clickTypeIn == ClickType.QUICK_MOVE) {
				if (InventoryUtilities.canFullyInsertItemIntoPlayerInventory(recipe.getRecipeOutput().copy(), player.inventory)) {
					// Craft the output.
					if (!getTileEntity().getWorld().isRemote) {
						ItemStack craftedResult = getTileEntity().craftItem();
						player.addItemStackToInventory(craftedResult);
						// Tell the slot we crafted.
						outputSlot.onCrafted(player, craftedResult);
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
		// Update the output slot if this is NOT the auto variant.
		if (!getPlayerInventory().player.world.isRemote && getType() == TYPE.getType()) {
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

}
