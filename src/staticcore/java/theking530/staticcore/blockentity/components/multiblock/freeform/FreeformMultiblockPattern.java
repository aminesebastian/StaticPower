package theking530.staticcore.blockentity.components.multiblock.freeform;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.multiblock.AbstractMultiblockPattern;
import theking530.staticcore.blockentity.components.multiblock.MultiblockBlockStateProperties;
import theking530.staticcore.blockentity.components.multiblock.MultiblockMatchClass;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.MultiblockStateBuilder;

public class FreeformMultiblockPattern extends AbstractMultiblockPattern {
	private Map<Character, MultiblockMatchClass> definitions;
	private Character masterKey;

	public FreeformMultiblockPattern(Character masterKey) {
		this.masterKey = masterKey;
		definitions = new HashMap<>();
	}

	public FreeformMultiblockPattern addDefinition(char character, TagKey<Block> block) {
		return addDefinition(character, block, 1, Integer.MAX_VALUE, false);
	}

	public FreeformMultiblockPattern addDefinition(char character, TagKey<Block> block, boolean optional) {
		return addDefinition(character, block, 1, Integer.MAX_VALUE, optional);
	}

	public FreeformMultiblockPattern addDefinition(char character, TagKey<Block> block, int minCount) {
		return addDefinition(character, block, minCount, Integer.MAX_VALUE, false);
	}

	public FreeformMultiblockPattern addDefinition(char character, TagKey<Block> block, int minCount, int maxCount) {
		return addDefinition(character, block, minCount, maxCount, false);
	}

	public FreeformMultiblockPattern addDefinition(char character, TagKey<Block> block, int minCount, int maxCount,
			boolean optional) {
		if (!definitions.containsKey(character)) {
			definitions.put(character, new MultiblockMatchClass(character, minCount, maxCount, optional));
		}
		definitions.get(character).addMatch(block);
		return this;
	}

	public <T extends Block> FreeformMultiblockPattern addDefinition(char character, Supplier<T> block) {
		return addDefinition(character, block, 1, Integer.MAX_VALUE, false);
	}

	public <T extends Block> FreeformMultiblockPattern addDefinition(char character, Supplier<T> block,
			boolean optional) {
		return addDefinition(character, block, 1, Integer.MAX_VALUE, optional);
	}

	public <T extends Block> FreeformMultiblockPattern addDefinition(char character, Supplier<T> block, int minCount) {
		return addDefinition(character, block, minCount, Integer.MAX_VALUE, false);
	}

	public <T extends Block> FreeformMultiblockPattern addDefinition(char character, Supplier<T> block, int minCount,
			int maxCount) {
		return addDefinition(character, block, minCount, maxCount, false);
	}

	public <T extends Block> FreeformMultiblockPattern addDefinition(char character, Supplier<T> block, int minCount,
			int maxCount, boolean optional) {
		if (!definitions.containsKey(character)) {
			definitions.put(character, new MultiblockMatchClass(character, minCount, maxCount, optional));
		}
		definitions.get(character).addMatch(() -> block.get().defaultBlockState());
		return this;
	}

	@Override
	public boolean isValidBlockForPosition(Level level, BlockState state, BlockPos absolutePos, BlockPos relativePos,
			boolean bypassExistingCheck) {
		if (!bypassExistingCheck && state.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
			if (state.getValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				return false;
			}
		}

		return isValidBlock(state);
	}

	@Override
	public MultiblockState isStateStillValid(Level level, MultiblockState existingState) {
		return checkWellFormedInternal(level, existingState.getInitialPos(), true);
	}

	@Override
	public MultiblockState checkWellFormed(Level level, BlockPos startPos) {
		return checkWellFormedInternal(level, startPos, false);
	}

	private MultiblockState checkWellFormedInternal(Level level, BlockPos startPos, boolean bypassExistingCheck) {
		if (!isValidBlockForPosition(level, level.getBlockState(startPos), null, startPos, bypassExistingCheck)) {
			return MultiblockState.FAILED;
		}

		MultiblockStateBuilder builder = new MultiblockStateBuilder(this, startPos, Direction.NORTH);

		Stack<BlockPos> dfsStack = new Stack<>();
		dfsStack.add(startPos);

		Set<BlockPos> visited = new HashSet<>();

		while (!dfsStack.isEmpty()) {
			BlockPos testPos = dfsStack.pop();
			if (visited.contains(testPos)) {
				continue;
			}
			visited.add(testPos);
			BlockState testState = level.getBlockState(testPos);

			MultiblockMatchClass matchClass = getMatchClassForBlock(testState);
			builder.addEntry(testPos, testState, matchClass.getKey(), canBeMaster(testPos, testState));

			for (Direction dir : Direction.values()) {
				BlockPos newTestPos = testPos.relative(dir);
				if (isValidBlockForPosition(level, level.getBlockState(newTestPos), newTestPos, newTestPos,
						bypassExistingCheck)) {
					dfsStack.push(newTestPos);
				}
			}
		}

		return builder.result();
	}

	@Override
	public boolean isValidBlock(BlockState state) {
		for (MultiblockMatchClass matchClass : definitions.values()) {
			if (matchClass.matches(state)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int getMaxX() {
		return 0;
	}

	@Override
	public int getMaxY() {
		return 0;
	}

	@Override
	public int getMaxZ() {
		return 0;
	}

	@Override
	public boolean canBeMaster(BlockPos pos, BlockState state) {
		if (!definitions.containsKey(masterKey)) {
			return false;
		}

		MultiblockMatchClass matchClass = definitions.get(masterKey);
		return matchClass.matches(state);
	}

	@Override
	public Collection<MultiblockMatchClass> getMatchClasses() {
		return definitions.values();
	}

	@Nullable
	private MultiblockMatchClass getMatchClassForBlock(BlockState state) {
		for (Character character : definitions.keySet()) {
			MultiblockMatchClass matchClass = definitions.get(character);
			if (matchClass.matches(state)) {
				return matchClass;
			}
		}
		return null;
	}
}
