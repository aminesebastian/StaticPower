package theking530.staticcore.blockentity.components.multiblock;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IMultiBlockComponent<T extends BlockEntity> {
	public void multiBlockValidated(MultiBlockEntry<T> token);

	public void multiBlockBroken();

	public MultiBlockEntry<T> getToken();

	public default boolean hasToken() {
		return getToken() != null && getToken().isValid();
	}
}
