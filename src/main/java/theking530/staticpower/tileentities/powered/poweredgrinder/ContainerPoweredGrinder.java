package theking530.staticpower.tileentities.powered.poweredgrinder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticpower.client.container.StaticPowerTileEntityContainer;
import theking530.staticpower.client.container.slots.BatterySlot;
import theking530.staticpower.client.container.slots.OutputSlot;
import theking530.staticpower.client.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.client.container.slots.UpgradeSlot;
import theking530.staticpower.crafting.wrappers.RecipeMatchParameters;
import theking530.staticpower.crafting.wrappers.StaticPowerRecipeRegistry;
import theking530.staticpower.crafting.wrappers.grinder.GrinderRecipe;
import theking530.staticpower.initialization.ModContainerTypes;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerPoweredGrinder extends StaticPowerTileEntityContainer<TileEntityPoweredGrinder> {

	public ContainerPoweredGrinder(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityPoweredGrinder) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerPoweredGrinder(int windowId, PlayerInventory playerInventory, TileEntityPoweredGrinder owner) {
		super(ModContainerTypes.POWERED_GRINDER_CONTAINER, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 18) {
			@Override
			public boolean isItemValid(ItemStack itemStack) {
				return StaticPowerRecipeRegistry.getRecipe(GrinderRecipe.RECIPE_TYPE, new RecipeMatchParameters(itemStack)).isPresent();
			}
		});

		// Battery
		this.addSlot(new BatterySlot(getTileEntity().batteryInventory, 0, 8, 65));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 60));
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 1, 106, 46));
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 2, 54, 46));

		// Upgrades
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		this.addSlot(new UpgradeSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, PlayerInventory invPlayer, Slot slot, int slotIndex) {
		if (getTileEntity().getRecipe(stack).isPresent() && !mergeItemStack(stack, 0)) {
			return true;
		}
		if (EnergyHandlerItemStackUtilities.isValidStaticPowerEnergyContainingItemstack(stack) && !mergeItemStack(stack, 1)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !mergeItemStack(stack, 5, 8, false)) {
			return true;
		}
		return false;
	}
}
