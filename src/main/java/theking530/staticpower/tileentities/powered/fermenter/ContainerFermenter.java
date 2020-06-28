package theking530.staticpower.tileentities.powered.fermenter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.initialization.ModFluids;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerFermenter extends StaticPowerTileEntityContainer<TileEntityFermenter> {

	public ContainerFermenter(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityFermenter) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerFermenter(int windowId, PlayerInventory playerInventory, TileEntityFermenter owner) {
		super(ModContainerTypes.FERMENTER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, j + i * 3, 40 + j * 18, 21 + i * 18));
			}
		}
		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 115, 55));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		// Container Input and Output
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventory, Items.BUCKET, 0, -24, 11));
		addSlot(new OutputSlot(getTileEntity().fluidContainerInventory, ModFluids.Mash.Bucket, 1, -24, 43));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, -24, 76));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, -24, 94));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, -24, 112));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		if (getTileEntity().getRecipe(stack).isPresent() && !mergeItemStack(stack, 0, 10, false)) {
			return true;
		}
		if (EnergyHandlerItemStackUtilities.isValidStaticPowerEnergyContainingItemstack(stack) && !mergeItemStack(stack, 11)) {
			return true;
		}
		if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() && !mergeItemStack(stack, 12)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 14, 17, false)) {
			return true;
		}
		return false;
	}
}
