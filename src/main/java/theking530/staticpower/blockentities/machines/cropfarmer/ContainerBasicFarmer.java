package theking530.staticpower.blockentities.machines.cropfarmer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;

public class ContainerBasicFarmer extends StaticPowerTileEntityContainer<BlockEntityBasicFarmer> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerBasicFarmer, GuiBasicFarmer> TYPE = new ContainerTypeAllocator<>("machine_basic_farmer", ContainerBasicFarmer::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiBasicFarmer::new);
		}
	}

	public ContainerBasicFarmer(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityBasicFarmer) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerBasicFarmer(int windowId, Inventory playerInventory, BlockEntityBasicFarmer owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		for (int l = 0; l < 3; ++l) {
			for (int i1 = 0; i1 < 3; ++i1) {
				addSlot(new OutputSlot(getTileEntity().outputInventory, i1 + l * 3, 76 + i1 * 18, 20 + l * 18));
			}
		}

		// Hoe
		addSlot(new StaticPowerContainerSlot(new ItemStack(Items.IRON_HOE), 0.3f, getTileEntity().inputInventory, 0, 48, 20));

		// Axe
		addSlot(new StaticPowerContainerSlot(new ItemStack(Items.IRON_AXE), 0.3f, getTileEntity().inputInventory, 1, 48, 56));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 60));

		addAllPlayerSlots();
	}

	@Override
	protected boolean playerItemShiftClicked(ItemStack stack, Player player, Slot slot, int slotIndex) {
		if (stack.getItem() instanceof HoeItem && !mergeItemStack(stack, 11)) {
			return true;
		}
		if (stack.getItem() instanceof AxeItem && !mergeItemStack(stack, 12)) {
			return true;
		}
		return false;
	}
}
