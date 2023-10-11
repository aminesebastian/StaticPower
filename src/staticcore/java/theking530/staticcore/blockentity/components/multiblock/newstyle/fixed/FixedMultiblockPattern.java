package theking530.staticcore.blockentity.components.multiblock.newstyle.fixed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.components.multiblock.newstyle.AbstractMultiblockPattern;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockMatchClass;
import theking530.staticcore.blockentity.components.multiblock.newstyle.MultiblockState;

public class FixedMultiblockPattern extends AbstractMultiblockPattern {
	public class MultiBlockPatternLayer {
		private List<String> rows;

		public MultiBlockPatternLayer() {
			rows = new ArrayList<>();
		}

		public String getRow(int i) {
			return rows.get(i);
		}

		public int getRowCount() {
			return rows.size();
		}

		public MultiBlockPatternLayer addRow(String row) {
			rows.add(row);
			onRowAddedToLayer(this);
			return this;
		}

		@Override
		public String toString() {
			return "MultiBlockPatternLayer [rows=" + rows + "]";
		}
	}

	private List<MultiBlockPatternLayer> pattern;
	private Map<Character, List<MultiblockMatchClass>> definitions;
	private int maxX;
	private int maxY;
	private int maxZ;

	public FixedMultiblockPattern() {
		pattern = new ArrayList<>();
		definitions = new HashMap<>();
	}

	@Override
	public boolean isValidBlock(BlockState state) {
		for (List<MultiblockMatchClass> matches : definitions.values()) {
			for (MultiblockMatchClass matchClass : matches) {
				if (matchClass.matches(state)) {
					return true;
				}
			}
		}
		return false;
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
			definitions.put(character, new ArrayList<>());
		}
		definitions.get(character).add(new MultiblockMatchClass(block));
		return this;
	}

	public <T extends Block> FixedMultiblockPattern addDefinition(char character, Supplier<T> block) {
		if (!definitions.containsKey(character)) {
			definitions.put(character, new ArrayList<>());
		}

		definitions.get(character).add(new MultiblockMatchClass(() -> block.get().defaultBlockState()));
		return this;
	}

	public MultiBlockPatternLayer addLayer() {
		MultiBlockPatternLayer layer = new MultiBlockPatternLayer();
		pattern.add(layer);
		maxY = pattern.size();
		return layer;
	}

	protected void onRowAddedToLayer(MultiBlockPatternLayer layer) {
		maxX = Math.max(maxX, layer.getRowCount());
		maxZ = Math.max(maxZ, layer.getRow(layer.getRowCount() - 1).length());
	}

	@Override
	public MultiblockState checkWellFormed(Level level, BlockPos startPos) {
		if (definitions.isEmpty()) {
			return MultiblockState.FAILED;
		}
		if (pattern.size() == 0) {
			return MultiblockState.FAILED;
		}

		BlockPos offsetStartPos = startPos;
		Stack<BlockPos> dfsStack = new Stack<>();
		dfsStack.add(startPos);

		while (!dfsStack.isEmpty()) {
			BlockPos testPos = dfsStack.pop();
			if (!isValidBlock(level.getBlockState(testPos))) {
				continue;
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
						MultiblockState state = checkWellFormedOriented(level, dir, offsetStartPos.offset(x, y, z));
						if (state.isWellFormed()) {
							return state;
						}
					}
				}
			}
		}
		return MultiblockState.FAILED;
	}

	private MultiblockState checkWellFormedOriented(Level level, Direction orientation, BlockPos startPos) {
		TreeMap<BlockPos, BlockState> blocks = new TreeMap<>();
		for (int y = 0; y < pattern.size(); y++) {
			for (int x = 0; x < pattern.get(y).getRowCount(); x++) {
				for (int z = 0; z < pattern.get(y).getRow(x).length(); z++) {
					BlockPos testPos = startPos.offset(0, y, 0).relative(orientation, x)
							.relative(orientation.getClockWise(), z);
					BlockState testState = level.getBlockState(testPos);
					if (!isValidBlockForPosition(testState, orientation, new BlockPos(x, y, z))) {
						return MultiblockState.FAILED;
					}
					blocks.put(testPos, testState);
				}
			}
		}
		return new MultiblockState(startPos, orientation, blocks, true);
	}

	@Override
	public boolean isValidBlockForPosition(BlockState state, Direction facing, BlockPos relativePos) {
		Character patternValue = pattern.get(relativePos.getY()).getRow(relativePos.getX()).charAt(relativePos.getZ());
		if (Character.isWhitespace(patternValue)) {
			return state.isAir();
		}

		List<MultiblockMatchClass> definition = definitions.get(patternValue);
		for (MultiblockMatchClass tag : definition) {
			if (tag.matches(state)) {
				return true;
			}
		}
		return false;
	}
}
