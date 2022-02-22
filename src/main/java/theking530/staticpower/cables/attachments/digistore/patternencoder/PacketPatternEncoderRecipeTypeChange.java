package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder.RecipeEncodingType;

public class PacketPatternEncoderRecipeTypeChange extends NetworkMessage {
	protected RecipeEncodingType recipeType;
	protected int windowId;

	public PacketPatternEncoderRecipeTypeChange() {

	}

	public PacketPatternEncoderRecipeTypeChange(int windowId, RecipeEncodingType recipeType) {
		this.recipeType = recipeType;
		this.windowId = windowId;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(recipeType.ordinal());
		buffer.writeInt(windowId);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		recipeType = RecipeEncodingType.values()[buffer.readInt()];
		windowId = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			AbstractContainerMenu openContainer = ctx.get().getSender().containerMenu;
			if (openContainer != null && openContainer instanceof ContainerDigistorePatternEncoder && openContainer.containerId == windowId) {
				ContainerDigistorePatternEncoder encoderContainer = (ContainerDigistorePatternEncoder) openContainer;
				encoderContainer.setCurrentRecipeType(recipeType);
			}
		});
	}
}
