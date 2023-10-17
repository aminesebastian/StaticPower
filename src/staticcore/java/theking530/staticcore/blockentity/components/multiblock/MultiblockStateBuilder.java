package theking530.staticcore.blockentity.components.multiblock;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.multiblock.newstyle.AbstractMultiblockPattern;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiBlockFormationStatus;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockMatchClass;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;

public class MultiblockStateBuilder {
	private final AbstractMultiblockPattern pattern;
	private final MultiblockState mbState;
	private final Map<Character, Integer> counts;

	public MultiblockStateBuilder(AbstractMultiblockPattern pattern, BlockPos startPos, Direction facingDirection) {
		this.pattern = pattern;
		mbState = new MultiblockState(startPos, facingDirection, MultiBlockFormationStatus.OK);
		counts = new HashMap<>();
	}

	public void addEntry(BlockPos pos, BlockState state, Character key, boolean canBeMaster) {
		mbState.addEntry(pos, state, key, canBeMaster);
		counts.put(key, counts.getOrDefault(key, 0) + 1);
	}

	public MultiblockState result() {
		// Check to make sure our counts are valid.
		for (MultiblockMatchClass matchClass : pattern.getMatchClasses()) {
			int count = counts.getOrDefault(matchClass.getKey(), 0);
			if (count == 0 && matchClass.isOptional()) {
				continue;
			}

			if (count > matchClass.getMaxCount() || count < matchClass.getMinCount()) {
				return MultiblockState.FAILED;
			}
		}

		return mbState;
	}
}
