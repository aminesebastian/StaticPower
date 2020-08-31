package theking530.staticpower.tileentities.powered.autocrafter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;

public class ContainerAutoCraftingTable extends StaticPowerTileEntityContainer<TileEntityAutoCraftingTable> implements IJEIReipceTransferHandler {
	private List<ItemStack> lastCraftingPattern;

	public ContainerAutoCraftingTable(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityAutoCraftingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoCraftingTable(int windowId, PlayerInventory playerInventory, TileEntityAutoCraftingTable owner) {
		super(ModContainerTypes.AUTO_CRAFTING_TABLE_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		lastCraftingPattern = new ArrayList<ItemStack>();

		// Add the pattern slots.
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				addSlot(new PhantomSlot(getTileEntity().patternInventory, x + (y * 3), 44 + x * 18, 20 + y * 18, true));
				lastCraftingPattern.add(getTileEntity().patternInventory.getStackInSlot(x + (y * 3)));
			}
		}

		// Add the inventory slots.
		for (int i = 0; i < 9; i++) {
			addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, i, 8 + i * 18, 78));
		}

		// Add the output slot.
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 129, 38));

		// Add the battery slot.
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 57));

		// Player slots.
		addPlayerInventory(getPlayerInventory(), 8, 103);
		addPlayerHotbar(getPlayerInventory(), 8, 161);
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
