package theking530.staticpower.tileentities.nonpowered.solderingtable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.PhantomSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;
import theking530.staticpower.items.tools.ISolderingIron;

public abstract class AbstractContainerSolderingTable<T extends TileEntitySolderingTable> extends StaticPowerTileEntityContainer<T> implements IJEIReipceTransferHandler {
	private List<ItemStack> lastCraftingPattern;
	protected boolean enableSolderingIronSlot;
	protected int craftingGridXOffset;

	@SuppressWarnings("unchecked")
	public AbstractContainerSolderingTable(ContainerType<?> containerType, int windowId, PlayerInventory inv, PacketBuffer data) {
		this(containerType, windowId, inv, (T) resolveTileEntityFromDataPacket(inv, data));
	}

	public AbstractContainerSolderingTable(ContainerType<?> containerType, int windowId, PlayerInventory playerInventory, T owner) {
		super(containerType, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		lastCraftingPattern = new ArrayList<ItemStack>();

		// Add the pattern slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new PhantomSlot(getTileEntity().patternInventory, x + (y * 3), 62 + craftingGridXOffset + x * 18, 20 + y * 18));
				lastCraftingPattern.add(getTileEntity().patternInventory.getStackInSlot(x + (y * 3)));
			}
		}

		// Add the inventory slots.
		for (int i = 0; i < 9; i++) {
			addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, i, 8 + i * 18, 78));
		}

		// Add the soldering iron slot.
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.SolderingIron), 0.3f, getTileEntity().solderingIronInventory, 0, 8, 20).setEnabledState(enableSolderingIronSlot));

		addOutputSlot();

		// Player slots.
		addPlayerInventory(getPlayerInventory(), 8, 103);
		addPlayerHotbar(getPlayerInventory(), 8, 161);
	}

	protected abstract void addOutputSlot();

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex) {
		// Get the slot and the slot's contents.
		Slot slot = inventorySlots.get(slotIndex);
		ItemStack stack = slot.getStack();

		// If this is a soldering iron, place it in the soldering iron slot.
		if (stack.getItem() instanceof ISolderingIron && slotIndex != 18 && !mergeItemStack(stack, 18)) {
			return stack;
		}

		// If we shift clicked an item in the soldering table inventory, move it to the
		// player inventory. If we shift clicked in the player inventory, attempt to
		// move the item to the soldering iron inventory.
		if (slotIndex <= 18) {
			if (!mergeItemStack(stack, 19, 48, false)) {
				return stack;
			}
		} else {
			if (!mergeItemStack(stack, 9, 18, false)) {
				return stack;
			}
		}

		return stack;
	}

	@Override
	public void consumeJEITransferRecipe(ItemStack[][] recipe) {
		if (recipe.length == 9) {
			for (int i = 0; i < 9; i++) {
				// Skip holes in the recipe.
				if (recipe[i] == null) {
					getTileEntity().patternInventory.setStackInSlot(i, ItemStack.EMPTY);
				} else {
					getTileEntity().patternInventory.setStackInSlot(i, recipe[i][0]);
				}
			}
		}
	}
}
