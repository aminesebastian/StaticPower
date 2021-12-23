package theking530.staticpower.tileentities.nonpowered.vacuumchest;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.FilterItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.items.itemfilter.ItemFilter;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerVacuumChest extends StaticPowerTileEntityContainer<TileEntityVacuumChest> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerVacuumChest, GuiVacuumChest> TYPE = new ContainerTypeAllocator<>("chest_vacuum", ContainerVacuumChest::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiVacuumChest::new);
		}
	}

	public ContainerVacuumChest(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityVacuumChest) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerVacuumChest(int windowId, Inventory playerInventory, TileEntityVacuumChest owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new StaticPowerContainerSlot(getTileEntity().inventory, x + y * 9, 8 + x * 18, 20 + y * 18));
			}
		}
		this.addSlot(new FilterItemSlot(getTileEntity().filterSlotInventory, 0, 8, 78));

		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 116, 78));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 134, 78));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 78));

		this.addPlayerInventory(getPlayerInventory(), 8, 103);
		this.addPlayerHotbar(getPlayerInventory(), 8, 161);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (stack.getItem() instanceof ItemFilter && !mergeItemStack(stack, 27)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !moveItemStackTo(stack, 28, 31, false)) {
			return true;
		}
		if (!moveItemStackTo(stack, 0, 27, false)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}
}
