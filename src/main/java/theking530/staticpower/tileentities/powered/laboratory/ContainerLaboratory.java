package theking530.staticpower.tileentities.powered.laboratory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerTileEntityContainer;
import theking530.staticpower.container.slots.BatteryItemSlot;
import theking530.staticpower.container.slots.StaticPowerContainerSlot;
import theking530.staticpower.init.ModItems;

public class ContainerLaboratory extends StaticPowerTileEntityContainer<TileEntityLaboratory> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerLaboratory, GuiLaboratory> TYPE = new ContainerTypeAllocator<>("laboratory", ContainerLaboratory::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiLaboratory::new);
		}
	}

	public ContainerLaboratory(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, (TileEntityLaboratory) resolveTileEntityFromDataPacket(inv, data));
	}

	public ContainerLaboratory(int windowId, Inventory playerInventory, TileEntityLaboratory owner) {
		super(TYPE, windowId, playerInventory, owner);
	}

	@Override
	public void initializeContainer() {
		// Input
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier1), getTileEntity().inputInventory, 0, 32, 34));
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier2), getTileEntity().inputInventory, 1, 51, 34));
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier3), getTileEntity().inputInventory, 2, 70, 34));
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier4), getTileEntity().inputInventory, 3, 89, 34));
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier5), getTileEntity().inputInventory, 4, 108, 34));
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier6), getTileEntity().inputInventory, 5, 127, 34));
		this.addSlot(new StaticPowerContainerSlot(new ItemStack(ModItems.ResearchTier7), getTileEntity().inputInventory, 6, 146, 34));

		// Battery
		this.addSlot(new BatteryItemSlot(getTileEntity().batteryInventory, 0, 8, 64));

		addAllPlayerSlots();
	}
}
