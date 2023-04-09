package theking530.staticcore.blockentity.components.control.processing.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import theking530.staticcore.blockentity.components.control.processing.AbstractProcessingComponentSyncPacket;

public class BasicProcessingComponentSyncPacket extends AbstractProcessingComponentSyncPacket {

	public BasicProcessingComponentSyncPacket() {

	}

	public <T extends BasicProcessingComponent<?, ?>> BasicProcessingComponentSyncPacket(BlockPos pos, T component) {
		super(pos, component);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		super.encode(buffer);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		super.decode(buffer);
	}
}
