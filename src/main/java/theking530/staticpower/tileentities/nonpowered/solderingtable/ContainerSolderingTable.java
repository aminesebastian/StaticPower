package theking530.staticpower.tileentities.nonpowered.solderingtable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.world.World;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.PhantomSlot;
import theking530.staticpower.client.container.slots.SolderingTableOutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.initialization.ModItems;
import theking530.staticpower.items.tools.ISolderingIron;

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
					updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, craftResult);
				}
			});
		}

		// Add the soldering iron slot.
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.SolderingIron), 0.3f, getTileEntity().solderingIronInventory, 0, 8, 20) {

			public void onSlotChanged() {
				super.onSlotChanged();
				updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, craftResult);
			}
		});

		// Output slot.
		addSlot(new SolderingTableOutputSlot(this, getPlayerInventory().player, craftResult, 0, 129, 38) {
			public void onSlotChanged() {
				super.onSlotChanged();
				updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, craftResult);
			}
		});

		// Player slots.
		addPlayerInventory(getPlayerInventory(), 8, 103);
		addPlayerHotbar(getPlayerInventory(), 8, 161);

		// Initial update of the output slot.
		updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, craftResult);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void onCraftingPatternChanged() {
		// Update the output slot.
		updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, this.craftResult);
	}

	public void onItemCrafted(ItemStack output) {
		if (!getTileEntity().getWorld().isRemote) {
			if (getTileEntity().solderingIronInventory.getStackInSlot(0).attemptDamageItem(1, getTileEntity().getWorld().rand, null)) {
				getTileEntity().solderingIronInventory.setStackInSlot(0, ItemStack.EMPTY);
				updateOutputSlot(getPlayerInventory().player.world, getPlayerInventory().player, craftResult);
			}
		}
	}

	/**
	 * Handles checking for if the crafting pattern has changed.
	 */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (!getTileEntity().getWorld().isRemote) {
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
	protected void updateOutputSlot(World world, PlayerEntity player, CraftResultInventory outputInv) {
		if (!world.isRemote) {
			ItemStack output = ItemStack.EMPTY;
			// Set the slot contents on the server.
			if (getTileEntity().hasRequiredItems()) {
				output = getTileEntity().getCurrentRecipe().get().getRecipeOutput().copy();
				outputInv.setInventorySlotContents(0, output);
			}

			// Sync the slot.
			ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
			serverplayerentity.connection.sendPacket(new SSetSlotPacket(this.windowId, 19, output));
		}
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		// If this is a soldering iron, place it in the soldering iron slot.
		if (stack.getItem() instanceof ISolderingIron && !mergeItemStack(stack, 18)) {
			return true;
		}

		// Try to place it in the bottom inventory.
		if (!mergeItemStack(stack, 9, 48, false)) {
			return true;
		}

		return false;
	}
}
