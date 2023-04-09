package theking530.staticcore.blockentity.components.control.processing;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.network.NetworkMessage;

public abstract class AbstractProcessingComponentSyncPacket extends NetworkMessage {
	private BlockPos pos;
	private String componentName;
	private int maxProcessingTime;
	private int proccesingTime;
	private ProcessingCheckState processingState;

	public AbstractProcessingComponentSyncPacket() {

	}

	public AbstractProcessingComponentSyncPacket(BlockPos pos, AbstractProcessingComponent<?, ?> component) {
		this.pos = pos;
		this.componentName = component.getComponentName();
		this.proccesingTime = component.getCurrentProcessingTime();
		this.maxProcessingTime = component.getProcessingTime();
		this.processingState = component.getProcessingState();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
		buffer.writeUtf(componentName);
		buffer.writeInt(proccesingTime);
		buffer.writeInt(maxProcessingTime);
		processingState.toNetwork(buffer);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		pos = BlockPos.of(buffer.readLong());
		componentName = buffer.readUtf();
		proccesingTime = buffer.readInt();
		maxProcessingTime = buffer.readInt();
		processingState = ProcessingCheckState.fromNetwork(buffer);
	}

	@Override
	@SuppressWarnings({ "resource" })
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level world = Minecraft.getInstance().player.getCommandSenderWorld();
			if (world.getBlockEntity(pos) instanceof BlockEntityBase) {
				BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);
				AbstractProcessingComponent<?, ?> storageComponent = te
						.getComponent(AbstractProcessingComponent.class, componentName);
				storageComponent.recieveClientSynchronizeData(this);
			}
		});
	}

	public BlockPos getPos() {
		return pos;
	}

	public String getComponentName() {
		return componentName;
	}

	public int getMaxProcessingTime() {
		return maxProcessingTime;
	}

	public int getProccesingTime() {
		return proccesingTime;
	}

	public ProcessingCheckState getProcessingState() {
		return processingState;
	}
}
