package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import net.minecraft.tileentity.TileEntity;

public interface ITileEntityRendererProxy<T extends TileEntity> {
	public Function<?, ?> getRenderFactory();
}
