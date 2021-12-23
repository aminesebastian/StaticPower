package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import net.minecraft.world.level.block.entity.BlockEntity;

public class ServerTileEntityRendererProxy<T extends BlockEntity> implements ITileEntityRendererProxy<T> {
	@Override
	public Function<?, ?> getRenderFactory() {
		return null;
	}

}
