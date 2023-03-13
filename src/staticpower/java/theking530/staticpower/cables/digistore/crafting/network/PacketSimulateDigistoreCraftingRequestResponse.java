package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle.CraftingStepsBundleContainer;

public class PacketSimulateDigistoreCraftingRequestResponse extends NetworkMessage {
	protected int windowId;
	protected CraftingStepsBundleContainer bundles;

	public PacketSimulateDigistoreCraftingRequestResponse() {

	}

	public PacketSimulateDigistoreCraftingRequestResponse(int windowId, CraftingStepsBundleContainer bundles) {
		this.windowId = windowId;
		this.bundles = bundles;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeNbt(bundles.serialize());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		bundles = CraftingStepsBundleContainer.read(buffer.readNbt());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			AbstractContainerMenu container = Minecraft.getInstance().player.containerMenu;
			if (container instanceof ContainerCraftingAmount && container.containerId == windowId) {
				ContainerCraftingAmount craftingContainer = (ContainerCraftingAmount) container;
				craftingContainer.onCraftingResponseUpdated(bundles);
			}
		});
	}
}