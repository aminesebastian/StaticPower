package theking530.staticpower.cables.attachments.digistore.digistoreterminal.autocrafting;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.container.StaticPowerContainer;

public class ContainerCraftingAmount extends StaticPowerContainer {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCraftingAmount, GuiCraftingAmount> TYPE = new ContainerTypeAllocator<>("crafting_request", ContainerCraftingAmount::new,
			GuiCraftingAmount::new);

	public ContainerCraftingAmount(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
		super(TYPE, windowId, playerInventory);
	}

	@Override
	public void initializeContainer() {

	}
}
