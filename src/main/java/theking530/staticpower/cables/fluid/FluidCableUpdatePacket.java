package theking530.staticpower.cables.fluid;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.tileentities.components.ComponentUtilities;

public class FluidCableUpdatePacket extends NetworkMessage {
	private BlockPos position;
	private FluidStack fluid;
	private float filledPercentage;

	public FluidCableUpdatePacket(BlockPos position, FluidStack fluid, float filledPercentage) {
		this.position = position;
		this.fluid = fluid;
		this.filledPercentage = filledPercentage;
	}

	public FluidCableUpdatePacket() {

	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(position);
		buffer.writeFluidStack(fluid);
		buffer.writeFloat(filledPercentage);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		position = buffer.readBlockPos();
		fluid = buffer.readFluidStack();
		filledPercentage = buffer.readFloat();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.level.isAreaLoaded(position, 1)) {
				BlockEntity rawTileEntity = Minecraft.getInstance().player.level.getBlockEntity(position);
				ComponentUtilities.getComponent(FluidCableComponent.class, rawTileEntity).ifPresent(comp -> {
					comp.recieveUpdateRenderValues(fluid, filledPercentage);
				});
			}
		});
	}
}
