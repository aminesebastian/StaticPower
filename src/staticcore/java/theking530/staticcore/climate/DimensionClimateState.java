package theking530.staticcore.climate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.heat.HeatInfo;
import theking530.api.heat.HeatUtilities;
import theking530.staticcore.world.WorldUtilities;

public class DimensionClimateState implements INBTSerializable<CompoundTag> {
	private final Map<ChunkPos, ChunkClimateState> chunks;

	public DimensionClimateState() {
		chunks = new HashMap<>();
	}

	public boolean tick(Level level) {
		for (Entry<ChunkPos, ChunkClimateState> entry : chunks.entrySet()) {
			if (entry.getValue().getStates().isEmpty()) {
				continue;
			}

			Iterator<Entry<BlockPos, ClimateState>> iterator = entry.getValue().blocks.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<BlockPos, ClimateState> chunkEntry = iterator.next();
				BlockPos pos = chunkEntry.getKey();
				ClimateState state = getClimateState(level, pos);

				for (Direction dir : Direction.values()) {
					BlockPos target = pos.relative(dir);
					ClimateState otherState = getClimateState(level, target);
					float transfered = 0;
					;// HeatStorageUtilities.calculateHeatTransfer(state.getTemperature(),
						// 1,otherState.getTemperature(), 1);

					state.setTemperature(state.getTemperature() - transfered);
					otherState.setTemperature(otherState.getTemperature() + transfered);
				}
			}
		}
		return false;
	}

	public void addTemperature(Level level, BlockPos pos, float temperature, float conductivity) {
		ChunkClimateState chunkState = getChunk(pos);
		ClimateState state = chunkState.getState(level, pos);
		float existingTemperature = state.getTemperature();
		float transfer = 0.0f; // HeatStorageUtilities.calculateHeatTransfer(existingTemperature, conductivity,
								// existingTemperature, 10.0f);
		state.setTemperature(existingTemperature + transfer);
	}

	public ClimateState getClimateState(Level level, BlockPos pos) {
		ChunkClimateState chunk = getChunk(pos);
		return chunk.getState(level, pos);
	}

	protected ChunkClimateState getChunk(BlockPos pos) {
		return getChunk(WorldUtilities.getChunkPosFromBlockPos(pos));
	}

	protected ChunkClimateState getChunk(ChunkPos chunkPos) {
		if (!chunks.containsKey(chunkPos)) {
			chunks.put(chunkPos, new ChunkClimateState(chunkPos));
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

	public class ChunkClimateState implements INBTSerializable<CompoundTag> {
		private final ChunkPos chunkPos;
		private final Map<BlockPos, ClimateState> blocks;

		public ChunkClimateState(ChunkPos chunkPos) {
			this.chunkPos = chunkPos;
			blocks = new ConcurrentHashMap<>();
		}

		public ChunkPos getChunk() {
			return chunkPos;
		}

		public Map<BlockPos, ClimateState> getStates() {
			return blocks;
		}

		public ClimateState getState(Level level, BlockPos pos) {
			if (!blocks.containsKey(pos)) {
				if (!WorldUtilities.getChunkPosFromBlockPos(pos).equals(chunkPos)) {
					return null;
				}
				HeatInfo temp = HeatUtilities.getAmbientProperties(level, pos);
				ClimateState state = new ClimateState(temp.temperature());
				state.setTemperature(0);
				blocks.put(pos, state);
			}
			return blocks.get(pos);
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag output = new CompoundTag();
			ListTag blocksTag = new ListTag();

			blocks.entrySet().forEach(entry -> {
				CompoundTag block = new CompoundTag();
				block.putLong("pos", entry.getKey().asLong());
				block.putFloat("temp", entry.getValue().getTemperature());
				blocksTag.add(block);
			});
			output.put("blocks", blocksTag);
			return output;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			ListTag blocksTag = nbt.getList("blocks", Tag.TAG_COMPOUND);
			for (Tag rawBlockTag : blocksTag) {
				CompoundTag blockTag = (CompoundTag) rawBlockTag;
				BlockPos pos = BlockPos.of(blockTag.getLong("pos"));

				if (blocks.containsKey(pos)) {
					blocks.get(pos).setTemperature(blockTag.getFloat("temp"));
				} else {
					blocks.put(pos, new ClimateState(blockTag.getFloat("temp")));
				}
			}
		}
	}

}
