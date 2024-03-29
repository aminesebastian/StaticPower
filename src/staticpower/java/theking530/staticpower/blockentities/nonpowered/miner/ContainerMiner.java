package theking530.staticpower.blockentities.nonpowered.miner;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.container.StaticPowerTileEntityContainer;
import theking530.staticcore.container.slots.OutputSlot;
import theking530.staticcore.container.slots.StaticPowerContainerSlot;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.init.ModItems;

public class ContainerMiner extends StaticPowerTileEntityContainer<BlockEntityMiner> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerMiner, GuiMiner> TYPE = new ContainerTypeAllocator<>("machine_miner", ContainerMiner::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiMiner::new);
		}
	}

	public ContainerMiner(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (BlockEntityMiner) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerMiner(int windowId, Inventory playerInventory, BlockEntityMiner owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Fuel Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(Items.COAL), 0.3f, getTileEntity().fuelInventory, 0, 18, 32));

		// Drill Bit Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.IronDrillBit.get()), 0.3f, getTileEntity().drillBitInventory, 0, 142, 32));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 32));
		addAllPlayerSlots();
	}
}
