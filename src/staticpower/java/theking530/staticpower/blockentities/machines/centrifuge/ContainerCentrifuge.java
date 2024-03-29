package theking530.staticpower.blockentities.machines.centrifuge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.api.energy.item.EnergyHandlerItemStackUtilities;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.container.slots.UpgradeItemSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.items.upgrades.BaseUpgrade;

public class ContainerCentrifuge extends StaticPowerTileEntityContainer<BlockEntityCentrifuge> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCentrifuge, GuiCentrifuge> TYPE = new ContainerTypeAllocator<>(
			"machine_centrifuge", ContainerCentrifuge::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiCentrifuge::new);
		}
	}

	public ContainerCentrifuge(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityCentrifuge) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerCentrifuge(int windowId, Inventory playerInventory, BlockEntityCentrifuge owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 18));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventories.get(0), 0, 81, 60));
		this.addSlot(new OutputSlot(getTileEntity().outputInventories.get(1), 0, 107, 60));
		this.addSlot(new OutputSlot(getTileEntity().outputInventories.get(2), 0, 55, 60));

		// Upgrades
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 0, 152, 12));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 1, 152, 32));
		this.addSlot(new UpgradeItemSlot(getTileEntity().upgradesInventory, 2, 152, 52));

		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (EnergyHandlerItemStackUtilities.isEnergyContainer(stack) && !mergeItemStack(stack, 1)) {
			return true;
		}
		if (stack.getItem() instanceof BaseUpgrade && !moveItemStackTo(stack, 5, 8, false)) {
			return true;
		}
		return false;
	}
}
