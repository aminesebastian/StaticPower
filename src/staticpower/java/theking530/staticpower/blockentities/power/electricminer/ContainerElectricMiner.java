package theking530.staticpower.blockentities.power.electricminer;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.init.ModItems;

public class ContainerElectricMiner extends StaticPowerTileEntityContainer<BlockEntityElectricMiner> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerElectricMiner, GuiElectricMiner> TYPE = new ContainerTypeAllocator<>("machine_electric_miner", ContainerElectricMiner::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiElectricMiner::new);
		}
	}

	public ContainerElectricMiner(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityElectricMiner) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerElectricMiner(int windowId, Inventory playerInventory, BlockEntityElectricMiner owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Drill Bit Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.IronDrillBit.get()), 0.3f, getTileEntity().drillBitInventory, 0, 142, 32));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 32));

		addAllPlayerSlots();
	}
}
