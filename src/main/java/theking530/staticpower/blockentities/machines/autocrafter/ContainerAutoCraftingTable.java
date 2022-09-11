package theking530.staticpower.blockentities.machines.autocrafter;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.PhantomSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.integration.JEI.IJEIReipceTransferHandler;

public class ContainerAutoCraftingTable extends StaticPowerTileEntityContainer<BlockEntityAutoCraftingTable> implements IJEIReipceTransferHandler {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAutoCraftingTable, GuiAutoCraftingTable> TYPE = new ContainerTypeAllocator<>("machine_industrial_crafting_table",
			ContainerAutoCraftingTable::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiAutoCraftingTable::new);
		}
	}

	private List<ItemStack> lastCraftingPattern;

	public ContainerAutoCraftingTable(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityAutoCraftingTable) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoCraftingTable(int windowId, Inventory playerInventory, BlockEntityAutoCraftingTable owner) {
		super(TYPE, windowId, playerInventory, owner);
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
		addAllPlayerSlots();
	}

	@Override
	public void consumeJEITransferRecipe(Player entity, ItemStack[][] recipe) {
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
