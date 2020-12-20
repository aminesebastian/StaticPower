package theking530.staticpower.cables.digistore.crafting.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
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
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeCompoundTag(bundle.serialize());
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		bundle = CraftingStepsBundle.read(buffer.readCompoundTag());
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			Container container = serverPlayer.openContainer;
			if (container instanceof ContainerCraftingAmount && container.windowId == windowId) {
				ContainerCraftingAmount caftingContainer = (ContainerCraftingAmount) container;
				caftingContainer.makeRequest(bundle);
			}
		});
	}
}
