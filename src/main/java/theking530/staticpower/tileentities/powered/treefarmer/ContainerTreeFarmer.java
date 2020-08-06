package theking530.staticpower.tileentities.powered.treefarmer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModContainerTypes;
import theking530.staticpower.init.ModTags;

public class ContainerTreeFarmer extends StaticPowerTileEntityContainer<TileEntityTreeFarm> {

	public ContainerTreeFarmer(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityTreeFarm) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTreeFarmer(int windowId, PlayerInventory playerInventory, TileEntityTreeFarm owner) {
		super(ModContainerTypes.TREE_FARMER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Inputs
		for (int l = 0; l < 3; ++l) {
			for (int i1 = 0; i1 < 3; ++i1) {
				addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1 + i1 + l * 3, 30 + i1 * 18, 20 + l * 18));
			}
		}

		// Output
		for (int l = 0; l < 3; ++l) {
			for (int i1 = 0; i1 < 3; ++i1) {
				addSlot(new OutputSlot(getTileEntity().outputInventory, i1 + l * 3, 90 + i1 * 18, 20 + l * 18));
			}
		}

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));


		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (ModTags.SAPLING.contains(stack.getItem()) && !mergeItemStack(stack, 0, 10, false)) {
			return true;
		}
		if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null).isPresent() && !mergeItemStack(stack, 18)) {
			return true;
		}
		if (stack.getItem() instanceof AxeItem && !mergeItemStack(stack, 20)) {
			return true;
		}
		return false;
	}
}
