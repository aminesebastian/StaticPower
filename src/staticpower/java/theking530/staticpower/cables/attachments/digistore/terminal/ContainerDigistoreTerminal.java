package theking530.staticpower.cables.attachments.digistore.terminal;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.blockentity.components.AbstractCableProviderComponent;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;

public class ContainerDigistoreTerminal extends AbstractContainerDigistoreTerminal<DigistoreTerminal> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerDigistoreTerminal, GuiDigistoreTerminal> TYPE = new ContainerTypeAllocator<>("digistore_terminal", ContainerDigistoreTerminal::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiDigistoreTerminal::new);
		}
	}

	public ContainerDigistoreTerminal(int windowId, Inventory inv, FriendlyByteBuf data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerDigistoreTerminal(int windowId, Inventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}
}
