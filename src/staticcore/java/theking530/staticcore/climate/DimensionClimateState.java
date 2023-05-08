package theking530.staticcore.climate;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.heat.HeatStorageUtilities;

public class DimensionClimateState implements INBTSerializable<CompoundTag> {
	private final Level level;
	private final Map<ChunkPos, ChunkClimateState> chunks;

	public DimensionClimateState(Level level) {
		chunks = new HashMap<>();
		this.level = level;
	}

	public boolean tick(Level level) {
		boolean dirty = false;
		for (Entry<ChunkPos, ChunkClimateState> entry : chunks.entrySet()) {
			if (entry.getValue().tick()) {
				dirty = true;
			}
		}
		return dirty;
	}

	public void addTemperature(BlockPos pos, float temperature, float conductivity) {
		ChunkClimateState chunkState = getChunk(pos);
		ClimateState state = chunkState.getState(pos);
		float existingTemperature = state.getTemperature();
		float transfer = HeatStorageUtilities.calculateHeatTransfer(existingTemperature, conductivity,
				existingTemperature, 10.0f);
		state.setTemperature(existingTemperature + transfer);
	}

	public ChunkClimateState getChunk(BlockPos pos) {
		ChunkPos chunkPos = level.getChunk(pos).getPos();
		return getChunk(chunkPos);
	}

	public ChunkClimateState getChunk(ChunkPos chunkPos) {
		if (!chunks.containsKey(chunkPos)) {
			chunks.put(chunkPos, new ChunkClimateState(level, chunkPos));
		}
		return chunks.get(chunkPos);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		ListTag chunksList = new ListTag();

		for (Entry<ChunkPos, ChunkClimateState> entry : chunks.entrySet()) {
			CompoundTag block = new CompoundTag();
			block.putLong("pos", entry.getKey().toLong());
			block.put("state", entry.getValue().serializeNBT());
			chunksList.add(block);
		}
		output.put("chunks", chunksList);

		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		CompoundTag chunksTag = nbt.getCompound("chunks");

		for (Entry<ChunkPos, ChunkClimateState> entry : chunks.entrySet()) {
			if (chunksTag.contains(entry.getKey().toString())) {
				entry.getValue().deserializeNBT(chunksTag.getCompound(entry.getKey().toString()));
			}
		}
	}
}
