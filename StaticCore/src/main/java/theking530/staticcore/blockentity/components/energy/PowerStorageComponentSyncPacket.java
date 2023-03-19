package theking530.staticcore.blockentity.components.energy;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.network.NetworkMessage;

public class PowerStorageComponentSyncPacket extends NetworkMessage {
	private BlockPos pos;
	private String componentName;
	private CompoundTag data;

	public PowerStorageComponentSyncPacket() {

	}

	public PowerStorageComponentSyncPacket(BlockPos pos, PowerStorageComponent component) {
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
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level world = Minecraft.getInstance().player.getCommandSenderWorld();
			if (world.getBlockEntity(pos) instanceof BlockEntityBase) {
				BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);
				PowerStorageComponent storageComponent = te.getComponent(PowerStorageComponent.class, componentName);
				storageComponent.recieveClientSynchronizeData(data, true);
			}
		});
	}
}
