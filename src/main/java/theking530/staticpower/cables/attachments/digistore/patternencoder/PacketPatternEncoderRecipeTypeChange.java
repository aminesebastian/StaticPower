package theking530.staticpower.cables.attachments.digistore.patternencoder;

import java.util.function.Supplier;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.cables.attachments.digistore.patternencoder.DigistorePatternEncoder.RecipeEncodingType;
import theking530.staticpower.network.NetworkMessage;

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
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(recipeType.ordinal());
		buffer.writeInt(windowId);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		recipeType = RecipeEncodingType.values()[buffer.readInt()];
		windowId = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Container openContainer = ctx.get().getSender().openContainer;
			if (openContainer != null && openContainer instanceof ContainerDigistorePatternEncoder && openContainer.windowId == windowId) {
				ContainerDigistorePatternEncoder encoderContainer = (ContainerDigistorePatternEncoder) openContainer;
				encoderContainer.setCurrentRecipeType(recipeType);
			}
		});
	}
}
