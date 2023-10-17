package theking530.staticcore.blockentity.components.multiblock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import theking530.staticcore.StaticCore;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState.MultiblockStateEntry;
import theking530.staticcore.utilities.NBTUtilities;

public class MultiblockManager extends SavedData {

	private static final String PREFIX = StaticCore.MOD_ID + "_multiblocks";
	private Map<BlockPos, MultiblockState> blocksToMasterMap;

	public MultiblockManager() {
		this.blocksToMasterMap = new HashMap<>();
	}

	public static MultiblockManager get(ServerLevel level) {
		String name = getSaveNameForWorld(level);
		return level.getDataStorage().computeIfAbsent((tag) -> MultiblockManager.load(tag, name, level),
				() -> new MultiblockManager(), name);
	}

	public boolean containsBlock(BlockPos pos) {
		return this.blocksToMasterMap.containsKey(pos);
	}

	public MultiblockState getMultiblockState(BlockPos pos) {
		return this.blocksToMasterMap.getOrDefault(pos, null);
	}

	public void addMultiblockState(MultiblockState state) {
		for (MultiblockStateEntry entry : state.getBlocks()) {
			blocksToMasterMap.put(entry.pos(), state);
		}
	}

	public void removeMultiblockState(MultiblockState state) {
		for (MultiblockStateEntry entry : state.getBlocks()) {
			blocksToMasterMap.remove(entry.pos());
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag serializedStates = NBTUtilities.serialize(blocksToMasterMap.values(), (entry) -> {
			return entry.serialize();
		});
		tag.put("states", serializedStates);

		return tag;
	}

	public static MultiblockManager load(CompoundTag tag, String name, Level world) {
		MultiblockManager output = new MultiblockManager();
		List<MultiblockState> states = NBTUtilities.deserialize(tag.getList("states", Tag.TAG_COMPOUND), (stateTag) -> {
			return MultiblockState.deserialize((CompoundTag) stateTag);
		});

		for (MultiblockState state : states) {
			for (MultiblockStateEntry entry : state.getBlocks()) {
				output.blocksToMasterMap.put(entry.pos(), state);
			}
		}

		return output;
	}

	private static String getSaveNameForWorld(Level level) {
		return PREFIX + "_" + level.dimension().location().getNamespace() + "_"
				+ level.dimension().location().getPath();
	}
}
