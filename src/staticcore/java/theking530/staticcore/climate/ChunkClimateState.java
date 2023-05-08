package theking530.staticcore.climate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.HeatStorageUtilities.HeatQuery;

public class ChunkClimateState implements INBTSerializable<CompoundTag> {
	private final Level level;
	private final ChunkPos chunkPos;
	private final Map<BlockPos, ClimateState> blocks;

	public ChunkClimateState(Level level, ChunkPos chunkPos) {
		this.level = level;
		this.chunkPos = chunkPos;
		blocks = new HashMap<>();
	}

	public boolean tick() {
		if (blocks.isEmpty()) {
			return false;
		}

		List<BlockPos> positions = new LinkedList<>(blocks.keySet());
		for (BlockPos pos : positions) {
			ClimateState state = blocks.get(pos);

			for (Direction dir : Direction.values()) {
				BlockPos target = pos.relative(dir);
				ClimateState otherState = getState(target);
				float transfered = HeatStorageUtilities.calculateHeatTransfer(state.getTemperature(), 1,
						otherState.getTemperature(), 1);

				state.setTemperature(state.getTemperature() - transfered);
				otherState.setTemperature(otherState.getTemperature() + transfered);
			}
		}
		return false;
	}

	public ChunkPos getChunk() {
		return chunkPos;
	}

	public ClimateState getState(BlockPos pos) {
		if (!blocks.containsKey(pos)) {
			if (!level.getChunk(pos).getPos().equals(chunkPos)) {
				return null;
			}
			HeatQuery temp = HeatStorageUtilities.getBiomeAmbientTemperature(level, pos);
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
