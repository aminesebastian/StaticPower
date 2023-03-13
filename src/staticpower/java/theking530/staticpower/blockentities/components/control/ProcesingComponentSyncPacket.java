package theking530.staticpower.blockentities.components.control;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.blockentities.BlockEntityBase;
import theking530.staticpower.blockentities.components.control.processing.AbstractProcesingComponent;

public class ProcesingComponentSyncPacket extends NetworkMessage {
	private BlockPos pos;
	private String componentName;
	private CompoundTag data;

	public ProcesingComponentSyncPacket() {

	}

	public ProcesingComponentSyncPacket(BlockPos pos, AbstractProcesingComponent<?> component) {
		this.pos = pos;
		this.componentName = component.getComponentName();
		this.data = new CompoundTag();
		component.serializeClientSynchronizeData(data, true);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
		buffer.writeNbt(data);
		buffer.writeUtf(componentName);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		pos = BlockPos.of(buffer.readLong());
		data = buffer.readNbt();
		componentName = buffer.readUtf();
	}

	@Override
	@SuppressWarnings("resource")
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level world = Minecraft.getInstance().player.getCommandSenderWorld();
			if (world.getBlockEntity(pos) instanceof BlockEntityBase) {
				BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);
				AbstractProcesingComponent<?> storageComponent = te.getComponent(AbstractProcesingComponent.class, componentName);
				storageComponent.recieveClientSynchronizeData(data, true);
			}
		});
	}
}
