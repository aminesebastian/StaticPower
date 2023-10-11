package theking530.staticcore.blockentity.components.multiblock.newstyle;

import java.util.Objects;
import java.util.TreeMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockState {
	public static final MultiblockState FAILED = new MultiblockState(BlockPos.ZERO, null, new TreeMap<>(), false);
	private final BlockPos initialPos;
	private final Direction facingDirection;
	private final TreeMap<BlockPos, BlockState> blocks;
	private final boolean wellFormed;

	public MultiblockState(BlockPos initialPos, Direction facingDirection, TreeMap<BlockPos, BlockState> blocks,
			boolean wellFormed) {
		this.facingDirection = facingDirection;
		this.initialPos = initialPos;
		this.blocks = blocks;
		this.wellFormed = wellFormed;
	}

	public TreeMap<BlockPos, BlockState> getBlocks() {
		return blocks;
	}

	public boolean isWellFormed() {
		return wellFormed;
	}

	public BlockPos getInitialPos() {
		return initialPos;
	}

	public Direction getFacingDirection() {
		return facingDirection;
	}

	@Override
	public int hashCode() {
		return Objects.hash(blocks, wellFormed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MultiblockState other = (MultiblockState) obj;
		return blocks.equals(other.blocks) && wellFormed == other.wellFormed && facingDirection == other.facingDirection
				&& initialPos.equals(other.initialPos);
	}

	@Override
	public String toString() {
		return "MultiblockState [wellFormed=" + wellFormed + ", blocks=" + blocks + "]";
	}
}
