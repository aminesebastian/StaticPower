package theking530.staticpower.blockentities.machines.autosmith;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerAutoSmith extends StaticPowerTileEntityContainer<BlockEntityAutoSmith> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerAutoSmith, GuiAutoSmith> TYPE = new ContainerTypeAllocator<>("machine_auto_smith", ContainerAutoSmith::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiAutoSmith::new);
		}
	}

	public ContainerAutoSmith(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityAutoSmith) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerAutoSmith(int windowId, Inventory playerInventory, BlockEntityAutoSmith owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 0, 80, 18));

		// Modifier
		addSlot(new StaticPowerContainerSlot(getTileEntity().inputInventory, 1, 110, 34));

		// Output
		addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 65, 60));

		// Completed Output
		addSlot(new OutputSlot(getTileEntity().completedOutputInventory, 0, 95, 60));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		return super.playerItemShiftClicked(stack, player, slot, slotIndex);
	}
}
