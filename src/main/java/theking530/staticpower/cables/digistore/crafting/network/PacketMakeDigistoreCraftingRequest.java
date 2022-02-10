package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting.ContainerCraftingAmount;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle;
import theking530.staticpower.network.NetworkMessage;

public class PacketMakeDigistoreCraftingRequest extends NetworkMessage {
	protected int windowId;
	protected CraftingStepsBundle bundle;

	public PacketMakeDigistoreCraftingRequest() {

	}

	public PacketMakeDigistoreCraftingRequest(int windowId, CraftingStepsBundle bundle) {
		this.windowId = windowId;
		this.bundle = bundle;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeNbt(bundle.serialize());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		bundle = CraftingStepsBundle.read(buffer.readNbt());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			AbstractContainerMenu container = serverPlayer.containerMenu;
			if (container instanceof ContainerCraftingAmount && container.containerId == windowId) {
				ContainerCraftingAmount caftingContainer = (ContainerCraftingAmount) container;
				caftingContainer.makeRequest(bundle);
			}
		});
	}
}
