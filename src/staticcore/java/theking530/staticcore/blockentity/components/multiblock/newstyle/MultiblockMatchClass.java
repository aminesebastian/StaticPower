package theking530.staticcore.blockentity.components.multiblock.newstyle;

import java.util.function.Supplier;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.utilities.tags.TagUtilities;

public class MultiblockMatchClass {
	private TagKey<Block> tag;
	private Supplier<BlockState> block;

	public MultiblockMatchClass(TagKey<Block> tag) {
		this.tag = tag;
	}

	public MultiblockMatchClass(Supplier<BlockState> block) {
		this.block = block;
	}

	public boolean matches(BlockState state) {
		if (tag != null) {
			return TagUtilities.matches(tag, state.getBlock());
		} else if (block != null) {
			return block.get().getBlock() == state.getBlock();
		}
		return false;
	}

	@Override
	public String toString() {
		return "MultiBlockMatchClass [tag=" + tag + ", block=" + block + "]";
	}
}