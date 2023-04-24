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
	private Timer proccesingTimer;
	private ProcessingCheckState processingState;
	private boolean performedWorkLastTick;

	public AbstractProcessingComponentSyncPacket() {

	}

	public AbstractProcessingComponentSyncPacket(BlockPos pos, AbstractProcessingComponent<?, ?> component) {
		this.pos = pos;
		this.componentName = component.getComponentName();
		this.proccesingTimer = component.getProcessingTimer();
		this.processingState = component.getProcessingState();
		this.performedWorkLastTick = component.performedWorkLastTick();
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
		buffer.writeUtf(componentName);
		proccesingTimer.encode(buffer);
		processingState.toNetwork(buffer);
		buffer.writeBoolean(performedWorkLastTick);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		pos = BlockPos.of(buffer.readLong());
		componentName = buffer.readUtf();
		proccesingTimer = Timer.decode(buffer);
		processingState = ProcessingCheckState.fromNetwork(buffer);
		performedWorkLastTick = buffer.readBoolean();
	}

	@Override
	@SuppressWarnings({ "resource" })
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Level world = Minecraft.getInstance().player.getCommandSenderWorld();
			if (world.getBlockEntity(pos) instanceof BlockEntityBase) {
				BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);
				AbstractProcessingComponent<?, ?> storageComponent = te.getComponent(AbstractProcessingComponent.class,
						componentName);
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

	public Timer getProccesingTime() {
		return proccesingTimer;
	}

	public ProcessingCheckState getProcessingState() {
		return processingState;
	}

	public boolean getPerformedWorkLastTick() {
		return performedWorkLastTick;
	}
}
