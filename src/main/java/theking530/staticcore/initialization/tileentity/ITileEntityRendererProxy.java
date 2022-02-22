package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface ITileEntityRendererProxy<T extends BlockEntity> {
	public Function<?, ?> getRenderFactory();
}
