package theking530.staticpower.tileentities.powered.treefarmer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatteryItemSlot;
import theking530.staticpower.client.container.slots.FluidContainerSlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeItemSlot;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.initialization.ModTags;

public class ContainerTreeFarmer extends StaticPowerTileEntityContainer<TileEntityTreeFarm> {

	private Ingredient saplingIngredient;

	public ContainerTreeFarmer(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityTreeFarm) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerTreeFarmer(int windowId, PlayerInventory playerInventory, TileEntityTreeFarm owner) {
		super(ModContainerTypes.TREE_FARMER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		saplingIngredient = Ingredient.fromTag(ModTags.SAPLING);
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

		// FluidContainerSlots
		addSlot(new FluidContainerSlot(getTileEntity().fluidContainerInventoy, Items.WATER_BUCKET, 0, -24, 11));
		addSlot(new OutputSlot(getTileEntity().fluidContainerInventoy, Items.BUCKET, 1, -24, 43));

		// Axe
		addSlot(new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), 0.3f, getTileEntity().inputInventory, 0, -24, 75) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return itemStack.getItem() instanceof AxeItem;
			}
		});

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		// Upgrades
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, -24, 106));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, -24, 124));
		addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, -24, 142));

		addPlayerInventory(getPlayerInventory(), 8, 84);
		addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (saplingIngredient.test(stack) && !mergeItemStack(stack, 1, 10, false)) {
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
