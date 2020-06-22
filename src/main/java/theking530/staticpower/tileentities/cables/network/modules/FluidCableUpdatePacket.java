package theking530.staticpower.tileentities.cables.network.modules;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.tileentities.cables.fluid.FluidCableComponent;
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
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(position);
		buffer.writeFluidStack(fluid);
		buffer.writeFloat(filledPercentage);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		position = buffer.readBlockPos();
		fluid = buffer.readFluidStack();
		filledPercentage = buffer.readFloat();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Minecraft.getInstance().player.world.isAreaLoaded(position, 1)) {
				TileEntity rawTileEntity = Minecraft.getInstance().player.world.getTileEntity(position);
				ComponentUtilities.getComponent(FluidCableComponent.class, rawTileEntity).ifPresent(comp -> {
					comp.recieveUpdateRenderValues(fluid, filledPercentage);
				});
			}
		});
	}
}
