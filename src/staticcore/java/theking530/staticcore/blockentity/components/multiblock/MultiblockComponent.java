package theking530.staticcore.blockentity.components.multiblock;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.AbstractBlockEntityComponent;

public abstract class MultiblockComponent<T extends BlockEntity> extends AbstractBlockEntityComponent
		implements IMultiBlockComponent<T> {
	private MultiBlockEntry<T> token;
	private MultiBlockCache<T> multiBlockCache;

	public MultiblockComponent(String name) {
		super(name);
	}

	public void onRegistered(BlockEntityBase owner) {
		super.onRegistered(owner);
		multiBlockCache = new MultiBlockCache<T>((T) owner, this::isValidForMultiBlock, this::isWellFormed);
	}

	public CompoundTag serializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
		return nbt;
	}

	public void deserializeUpdateNbt(CompoundTag nbt, boolean fromUpdate) {
	}

	public boolean hasController() {
		return getToken() != null;
	}

	@Override
	public void multiBlockValidated(MultiBlockEntry<T> token) {
		this.token = token;
	}

	@Override
	public void multiBlockBroken() {
		token = null;
	}

	@Override
	public MultiBlockEntry<T> getToken() {
		return token;
	}

	@Override
	public void onOwningBlockEntityUnloaded() {
		if (hasToken()) {
			getToken().remove();
		}
	}

	protected abstract boolean isValidForMultiBlock(BlockPos pos, BlockState state, BlockEntity be);

	protected abstract MultiBlockFormationStatus isWellFormed(Map<BlockPos, MultiBlockEntry<T>> map);
}
