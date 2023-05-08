package theking530.staticcore.climate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;

public class ClimateDataReceievePacket extends NetworkMessage {
	protected ResourceLocation dimension;
	protected List<ClimateState> climateStates;

	public ClimateDataReceievePacket() {

	}

	public ClimateDataReceievePacket(ResourceLocation dimension, List<ClimateState> climateStates) {
		this.climateStates = climateStates;
		this.dimension = dimension;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(dimension.toString());
		buffer.writeInt(climateStates.size());
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		dimension = new ResourceLocation(buffer.readUtf());
		int stateCount = buffer.readInt();
		climateStates = new ArrayList<>(stateCount);
		for (int i = 0; i < stateCount; i++) {

		}
	}

	@SuppressWarnings("resource")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level level = Minecraft.getInstance().player.level;
			if (!level.dimension().location().equals(dimension)) {
				return;
			}
			
			
		});
	}
}
