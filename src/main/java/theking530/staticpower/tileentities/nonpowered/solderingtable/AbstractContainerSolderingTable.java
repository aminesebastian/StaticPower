package theking530.staticpower.tileentities.nonpowered.solderingtable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;

public abstract class AbstractContainerSolderingTable<T extends AbstractSolderingTable> extends StaticPowerTileEntityContainer<T> implements IJEIReipceTransferHandler {
	protected boolean enableSolderingIronSlot;
	protected int craftingGridXOffset;

	@SuppressWarnings("unchecked")
	public AbstractContainerSolderingTable(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int windowId, Inventory inv, FriendlyByteBuf data) {
		this(allocator, windowId, inv, (T) resolveTileEntityFromDataPacket(inv, data));
	}

	public AbstractContainerSolderingTable(ContainerTypeAllocator<? extends StaticPowerContainer, ?> allocator, int windowId, Inventory playerInventory, T owner) {
		super(allocator, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Add the inventory slots.
		for (int i = 0; i < 9; i++) {
			addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, i, 8 + i * 18, 78));
		}

		// Add the soldering iron slot.
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.SolderingIron), 0.3f, getTileEntity().solderingIronInventory, 0, 8, 20).setEnabledState(enableSolderingIronSlot));

		addOutputSlot();

		// Player slots.
		addPlayerInventory(getPlayerInventory(), 8, 113);
		addPlayerHotbar(getPlayerInventory(), 8, 171);
	}

	protected abstract void addOutputSlot();

	@Override
	public void consumeJEITransferRecipe(Player playerIn, ItemStack[][] recipe) {
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
