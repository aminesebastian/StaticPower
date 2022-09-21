package theking530.staticpower.blockentities.nonpowered.condenser;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerCondenser extends StaticPowerTileEntityContainer<BlockEntityCondenser> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCondenser, GuiCondenser> TYPE = new ContainerTypeAllocator<>("machine_condenser", ContainerCondenser::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiCondenser::new);
		}
	}

	public ContainerCondenser(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityCondenser) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCondenser(int windowId, Inventory playerInventory, BlockEntityCondenser owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 17));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 37));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 57));


		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !moveItemStackTo(stack, 5, 6, false)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !moveItemStackTo(stack, 2, 5, false)) {
			return true;
		}
		return false;
	}
}
