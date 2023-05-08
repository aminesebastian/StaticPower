package theking530.staticcore.climate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.climate.DimensionClimateState.ChunkClimateState;
import theking530.staticcore.data.gamedata.StaticCoreGameDataManager.StaticCoreDataAccessor;
import theking530.staticcore.network.NetworkMessage;

public class ClimateDataRequestPacket extends NetworkMessage {
	protected BlockPos pos;
	protected int radius;

	public ClimateDataRequestPacket() {

	}

	public ClimateDataRequestPacket(BlockPos pos, int radius) {
		this.pos = pos;
		this.radius = radius;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeLong(pos.asLong());
		buffer.writeInt(radius);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		pos = BlockPos.of(buffer.readLong());
		radius = buffer.readInt();
	}

	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerLevel level = ctx.get().getSender().getLevel();
			ClimateManager gameData = StaticCoreDataAccessor.getClient().getGameData(ClimateManager.ID);
			DimensionClimateState climateData = gameData.getClimateData(level);
			ChunkClimateState chunkData = climateData.getChunk(pos);
			
			List<ClimateState> climateStates = new ArrayList<>(radius * radius * radius);
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						climateStates.add(chunkData.getState(level, pos.offset(x, y, x)));
					}
				}
			}
			
			
		});
	}
}
