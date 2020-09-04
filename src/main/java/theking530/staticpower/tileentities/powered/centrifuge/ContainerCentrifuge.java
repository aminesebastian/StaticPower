package theking530.staticpower.tileentities.powered.centrifuge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.container.slots.UpgradeItemSlot;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.items.upgrades.BaseUpgrade;
import theking530.staticpower.items.utilities.EnergyHandlerItemStackUtilities;

public class ContainerCentrifuge extends StaticPowerTileEntityContainer<TileEntityCentrifuge> {
		@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCentrifuge, GuiCentrifuge> TYPE = new ContainerTypeAllocator<>("machine_centrifuge", ContainerCentrifuge::new, GuiCentrifuge::new);

	public ContainerCentrifuge(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityCentrifuge) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCentrifuge(int windowId, PlayerInventory playerInventory, TileEntityCentrifuge owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 18));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().firstOutputInventory, 0, 81, 60));
		this.addSlot(new OutputSlot(getTileEntity().secondOutputInventory, 0, 107, 60));
		this.addSlot(new OutputSlot(getTileEntity().thirdOutputInventory, 0, 55, 60));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, PlayerEntity player, Slot slot, int slotIndex) {
		if (getTileEntity().processingComponent.getRecipe(new RecipeMatchParameters(stack)).isPresent() && !mergeItemStack(stack, 0)) {
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
