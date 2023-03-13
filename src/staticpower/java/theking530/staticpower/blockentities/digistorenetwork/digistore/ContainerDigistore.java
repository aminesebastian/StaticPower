package theking530.staticpower.blockentities.digistorenetwork.digistore;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;

public class ContainerDigistore extends StaticPowerTileEntityContainer<BlockEntityDigistore> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistore, GuiDigistore> TYPE = new ContainerTypeAllocator<>("digistore", ContainerDigistore::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistore::new);
		}
	}

	public ContainerDigistore(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityDigistore) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerDigistore(int windowId, Inventory playerInventory, BlockEntityDigistore owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.BasicSingularDigistoreCard.get()), 0.3f, getTileEntity().inventory, 0, 80, 45));

		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		// Try to insert into inventory.
		ItemStack remaining = getTileEntity().insertItem(0, stack.copy(), false);

		// If any amount was inserted, modify the stack and return true. Otherwise,
		// resort to default behaviour.
		if (remaining.getCount() != remaining.getCount()) {
			stack.setCount(remaining.getCount());
			return true;
		} else {
			return super.playerItemShiftClicked(stack, player, slot, slotIndex);
		}
	}
}
