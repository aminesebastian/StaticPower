package theking530.staticcore.initialization.blockentity;

import java.util.function.Function;

import net.minecraft.world.level.block.entity.BlockEntity;

public class ServerBlockEntityRendererProxy<T extends BlockEntity> implements IBlockEntityRendererProxy<T> {
	@Override
	public Function<?, ?> getRenderFactory() {
		return null;
	}

}
