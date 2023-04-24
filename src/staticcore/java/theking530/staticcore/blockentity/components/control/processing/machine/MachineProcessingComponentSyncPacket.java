package theking530.staticcore.blockentity.components.control.processing.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import theking530.staticcore.blockentity.components.control.processing.AbstractProcessingComponentSyncPacket;

public class MachineProcessingComponentSyncPacket extends AbstractProcessingComponentSyncPacket {

	public MachineProcessingComponentSyncPacket() {

	}

	public <T extends AbstractMachineProcessingComponent<?, ?>> MachineProcessingComponentSyncPacket(BlockPos pos, T component) {
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
