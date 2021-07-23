package theking530.staticpower.cables.attachments.basicredstoneio;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.AbstractCableProviderComponent;
import theking530.staticpower.cables.attachments.AbstractCableAttachmentContainer;

public class ContainerBasicRedstoneIO extends AbstractCableAttachmentContainer<BasicRedstoneIO> {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerBasicRedstoneIO, GuiBasicRedstoneIO> TYPE = new ContainerTypeAllocator<>("cable_attachment_basic_redstone_io",
			ContainerBasicRedstoneIO::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiBasicRedstoneIO::new);
		}
	}

	public ContainerBasicRedstoneIO(int windowId, PlayerInventory inv, PacketBuffer data) {
		this(windowId, inv, getAttachmentItemStack(inv, data), getAttachmentSide(data), getCableComponent(inv, data));
	}

	public ContainerBasicRedstoneIO(int windowId, PlayerInventory playerInventory, ItemStack attachment, Direction attachmentSide, AbstractCableProviderComponent cableComponent) {
		super(TYPE, windowId, playerInventory, attachment, attachmentSide, cableComponent);
	}

	@Override
	public void initializeContainer() {
		addPlayerInventory(getPlayerInventory(), 8, 69);
		addPlayerHotbar(getPlayerInventory(), 8, 127);
	}
}
