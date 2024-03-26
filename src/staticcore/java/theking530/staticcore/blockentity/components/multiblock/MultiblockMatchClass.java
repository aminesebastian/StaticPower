package theking530.staticcore.blockentity.components.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.utilities.tags.TagUtilities;

public class MultiblockMatchClass {
	private final Character key;
	private final List<TagKey<Block>> tags;
	private final List<Supplier<BlockState>> blocks;
	private final int minCount;
	private final int maxCount;
	private final boolean optional;

	public MultiblockMatchClass(Character key, int minCount, int maxCount, boolean optional) {
		this.key = key;
		this.tags = new ArrayList<>();
		this.blocks = new ArrayList<>();
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.optional = optional;
	}

	public void addMatch(TagKey<Block> tag) {
		this.tags.add(tag);
	}

	public void addMatch(Supplier<BlockState> block) {
		this.blocks.add(block);
	}

	public Character getKey() {
		return key;
	}

	public int getMinCount() {
		return minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public boolean isOptional() {
		return optional;
	}

	public boolean matches(BlockState state) {
		if (Character.isWhitespace(key)) {
			return state.isAir();
		}

		for (TagKey<Block> tag : tags) {
			if (TagUtilities.matches(tag, state.getBlock())) {
				return true;
			}
		}

		for (Supplier<BlockState> block : blocks) {
			if (block.get().getBlock() == state.getBlock()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return "MultiblockMatchClass [key=" + key + ", tags=" + tags + ", blocks=" + blocks + ", minCount=" + minCount
				+ ", maxCount=" + maxCount + ", optional=" + optional + "]";
	}
}