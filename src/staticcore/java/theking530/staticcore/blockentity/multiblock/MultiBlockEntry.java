package theking530.staticcore.blockentity.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MultiBlockEntry<T extends BlockEntity> {
	private final MultiBlockCache<T> owningCache;
	private final BlockState state;
	private final BlockPos position;
	private boolean valid;

	public MultiBlockEntry(MultiBlockCache<T> owningCache, BlockState state, BlockPos position) {
		super();
		this.owningCache = owningCache;
		this.state = state;
		this.position = position;
		this.valid = true;
	}

	public BlockState getBlockState() {
		return state;
	}

	public boolean isValid() {
		return valid;
	}

	public void invalidate() {
		valid = false;
	}

	public T getController() {
		return owningCache.getController();
	}

	public BlockPos getPosition() {
		return position;
	}

	public void update() {
		owningCache.update();
	}

	public void remove() {
		owningCache.update();
	}
}