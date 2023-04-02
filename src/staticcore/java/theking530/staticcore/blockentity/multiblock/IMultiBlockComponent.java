package theking530.staticcore.blockentity.multiblock;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IMultiBlockComponent<T extends BlockEntity> {
	public void setToken(MultiBlockToken<T> token);

	public void clearToken();

	public MultiBlockToken<T> getToken();

	public default boolean hasToken() {
		return getToken() != null && getToken().isValid();
	}
}
