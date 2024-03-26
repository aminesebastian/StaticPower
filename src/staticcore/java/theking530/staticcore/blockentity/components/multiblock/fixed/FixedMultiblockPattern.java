package theking530.staticcore.blockentity.components.multiblock.fixed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.multiblock.AbstractMultiblockPattern;
import theking530.staticcore.blockentity.components.multiblock.MultiblockBlockStateProperties;
import theking530.staticcore.blockentity.components.multiblock.MultiblockMatchClass;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.MultiblockStateBuilder;
import theking530.staticcore.blockentity.components.multiblock.manager.MultiblockManager;
import theking530.staticcore.blockentity.components.multiblock.manager.ServerMultiblockManager;

public class FixedMultiblockPattern extends AbstractMultiblockPattern {
	private List<FixedMultiblockPatternLayer> layers;
	private Map<Character, MultiblockMatchClass> definitions;
	private Character masterKey;
	private int maxX;
	private int maxY;
	private int maxZ;

	public FixedMultiblockPattern(char masterKey) {
		layers = new ArrayList<>();
		definitions = new HashMap<>();
		this.masterKey = masterKey;
		maxX = -1;
		maxZ = -1;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}

	@Override
	public int getMaxZ() {
		return maxZ;
	}

	public FixedMultiblockPattern addDefinition(char character, TagKey<Block> block) {
		if (!definitions.containsKey(character)) {
			definitions.put(character, new MultiblockMatchClass(character, 0, Integer.MAX_VALUE, false));
		}
		definitions.get(character).addMatch(block);
		return this;
	}

	public <T extends Block> FixedMultiblockPattern addDefinition(char character, Supplier<T> block) {
		if (!definitions.containsKey(character)) {
			definitions.put(character, new MultiblockMatchClass(character, 0, Integer.MAX_VALUE, false));
		}

		definitions.get(character).addMatch(() -> block.get().defaultBlockState());
		return this;
	}

	public FixedMultiblockPatternLayer addLayer() {
		FixedMultiblockPatternLayer layer = new FixedMultiblockPatternLayer();
		layers.add(layer);
		return layer;
	}

	@Override
	public MultiblockState checkWellFormed(Level level, BlockPos startPos) {
		if (definitions.isEmpty()) {
			return MultiblockState.FAILED;
		}
		if (layers.size() == 0) {
			return MultiblockState.FAILED;
		}

		if (maxX == -1 || maxY == -1) {
			cacheSize();
		}

		BlockPos offsetStartPos = startPos;
		Stack<BlockPos> dfsStack = new Stack<>();
		dfsStack.add(startPos);

		while (!dfsStack.isEmpty()) {
			BlockPos testPos = dfsStack.pop();
			BlockState testState = level.getBlockState(testPos);
			if (!isValidBlock(testState)) {
				continue;
			}

			// Stop if we hit a block that's already in a multiblock.
			if (testState.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				if (testState.getValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
					continue;
				}
			}

			if (Math.abs(testPos.getX() - startPos.getX()) >= maxX) {
				continue;
			}

			if (Math.abs(testPos.getZ() - startPos.getZ()) >= maxZ) {
				continue;
			}
			if (Math.abs(testPos.getY() - startPos.getY()) >= maxY) {
				continue;
			}

			dfsStack.push(testPos.offset(-1, 0, 0));
			dfsStack.push(testPos.offset(0, 0, -1));
			dfsStack.push(testPos.offset(0, -1, 0));
			offsetStartPos = testPos;
		}

		for (int x = 0; x < maxX; x++) {
			for (int z = 0; z < maxZ; z++) {
				for (int y = 0; y < maxY; y++) {
					for (Direction dir : Direction.Plane.HORIZONTAL) {
						MultiblockState state = checkWellFormedOriented(level, dir, offsetStartPos.offset(x, y, z),
								false);
						if (state.isWellFormed()) {
							return state;
						}
					}
				}
			}
		}
		return MultiblockState.FAILED;
	}

	@Override
	public MultiblockState isStateStillValid(Level level, MultiblockState existingState) {
		if (!existingState.isWellFormed() || existingState.getBlocks().isEmpty()) {
			return existingState;
		}

		BlockPos initialPos = existingState.getInitialPos();
		return checkWellFormedOriented(level, existingState.getFacingDirection(), initialPos, true);
	}

	private MultiblockState checkWellFormedOriented(Level level, Direction orientation, BlockPos startPos,
			boolean bypassExistingCheck) {
		MultiblockStateBuilder builder = new MultiblockStateBuilder(this, startPos, orientation);

		for (int y = 0; y < layers.size(); y++) {
			for (int x = 0; x < layers.get(y).getRowCount(); x++) {
				for (int z = 0; z < layers.get(y).getRow(x).length(); z++) {
					BlockPos testPos = startPos.offset(0, y, 0).relative(orientation, x)
							.relative(orientation.getClockWise(), z);
					BlockState testState = level.getBlockState(testPos);
					Character patternValue = layers.get(y).getRow(x).charAt(z);
					if (!isValidBlockForPosition(level, testState, testPos, new BlockPos(x, y, z),
							bypassExistingCheck)) {
						return MultiblockState.FAILED;
					}
					builder.addEntry(testPos, testState, patternValue, canBeMaster(testPos, testState));
				}
			}
		}

		return builder.result();
	}

	@Override
	public boolean isValidBlockForPosition(Level level, BlockState state, BlockPos absolutePos, BlockPos relativePos,
			boolean bypassExistingCheck) {
		Character patternValue = layers.get(relativePos.getY()).getRow(relativePos.getX()).charAt(relativePos.getZ());
		if (Character.isWhitespace(patternValue)) {
			return state.isAir();
		}

		if (!bypassExistingCheck && state.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
			if (state.getValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				return false;
			}
		}

		if (!bypassExistingCheck && !level.isClientSide()) {
			ServerMultiblockManager mbManager = MultiblockManager.get((ServerLevel) level);
			if (mbManager.containsBlock(absolutePos)) {
				return false;
			}
		}

		return definitions.get(patternValue).matches(state);
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

	private void cacheSize() {
		maxY = layers.size();
		for (FixedMultiblockPatternLayer layer : layers) {
			maxX = Math.max(maxX, layer.getRowCount());
			maxZ = Math.max(maxZ, layer.getRow(layer.getRowCount() - 1).length());
		}
	}
}
