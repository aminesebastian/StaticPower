package theking530.staticpower.blockentities.nonpowered.evaporator;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.UpgradeItemSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerEvaporator extends StaticPowerTileEntityContainer<BlockEntityEvaporator> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerEvaporator, GuiEvaporator> TYPE = new ContainerTypeAllocator<>("machine_evaporator", ContainerEvaporator::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiEvaporator::new);
		}
	}

	public ContainerEvaporator(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityEvaporator) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerEvaporator(int windowId, Inventory playerInventory, BlockEntityEvaporator owner) {
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
