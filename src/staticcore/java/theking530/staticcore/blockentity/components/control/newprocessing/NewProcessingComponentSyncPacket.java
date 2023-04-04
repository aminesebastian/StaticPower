package theking530.staticcore.blockentity.components.control.newprocessing;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.network.NetworkMessage;

public class NewProcessingComponentSyncPacket<T extends NewProcessingComponentSyncPacket<T>> extends NetworkMessage {
	private BlockPos pos;
	private String componentName;

	public NewProcessingComponentSyncPacket() {

	}

	public NewProcessingComponentSyncPacket(BlockPos pos, NewAbstractProcessingComponent<?, T> component) {
		this.pos = pos;
		this.componentName = component.getComponentName();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
		buffer.writeUtf(componentName);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		pos = BlockPos.of(buffer.readLong());
		componentName = buffer.readUtf();
	}

	@Override
	@SuppressWarnings({ "resource", "unchecked" })
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level world = Minecraft.getInstance().player.getCommandSenderWorld();
			if (world.getBlockEntity(pos) instanceof BlockEntityBase) {
				BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);
				NewAbstractProcessingComponent<?, T> storageComponent = te
						.getComponent(NewAbstractProcessingComponent.class, componentName);
				storageComponent.recieveClientSynchronizeData((T) this);
			}
		});
	}
}
