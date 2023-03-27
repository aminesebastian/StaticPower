package theking530.staticcore.initialization.blockentity;

import java.util.function.Function;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityRendererProxy<T extends BlockEntity> {
	public Function<?, ?> getRenderFactory();
}
