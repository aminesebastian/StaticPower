package theking530.staticpower.tileentities.powered.electricminer;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.OutputSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;

public class ContainerElectricMiner extends StaticPowerTileEntityContainer<TileEntityElectricMiner> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerElectricMiner, GuiElectricMiner> TYPE = new ContainerTypeAllocator<>("machine_electric_miner", ContainerElectricMiner::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiElectricMiner::new);
		}
	}

	public ContainerElectricMiner(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, (TileEntityElectricMiner) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerElectricMiner(int windowId, PlayerInventory playerInventory, TileEntityElectricMiner owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Drill Bit Slot
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.IronDrillBit), 0.3f, getTileEntity().drillBitInventory, 0, 142, 32));

		// Battery
		addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		// Output
		this.addSlot(new OutputSlot(getTileEntity().outputInventory, 0, 80, 32));

		this.addPlayerInventory(getPlayerInventory(), 8, 84);
		this.addPlayerHotbar(getPlayerInventory(), 8, 142);
	}
}
