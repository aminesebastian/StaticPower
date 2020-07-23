package theking530.staticpower.tileentities.powered.basicfarmer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.initialization.ModContainerTypes;

public class ContainerBasicFarmer extends StaticPowerTileEntityContainer<TileEntityBasicFarmer> {
	public ContainerBasicFarmer(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityBasicFarmer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerBasicFarmer(int windowId, PlayerInventory playerInventory, TileEntityBasicFarmer owner) {
		super(ModContainerTypes.BASIC_FARMER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		for (int l = 0; l < 3; ++l) {
			for (int i1 = 0; i1 < 3; ++i1) {
				addSlot(new OutputSlot(getTileEntity().outputInventory, i1 + l * 3, 76 + i1 * 18, 20 + l * 18));
			}
		}

		// FluidContainerSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.WATER_BUCKET, 0, -24, 11));
		addSlot(new OutputSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 1, -24, 43));

		// Hoe
		addSlot(new StaticPowerContainerSlot(new ItemStack(Items.IRON_HOE), 0.3f, getTileEntity().inputInventory, 0, 48, 20) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return itemStack.getItem() instanceof HoeItem;
			}
		});

		// Axe
		addSlot(new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), 0.3f, getTileEntity().inputInventory, 1, 48, 56) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return itemStack.getItem() instanceof AxeItem;
			}
		});

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, -24, 76));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, -24, 94));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, -24, 112));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (stack.getItem() instanceof HoeItem && !mergeItemStack(stack, 11)) {
			return true;
		}
		if (stack.getItem() instanceof AxeItem && !mergeItemStack(stack, 12)) {
			return true;
		}
		return false;
	}
}
