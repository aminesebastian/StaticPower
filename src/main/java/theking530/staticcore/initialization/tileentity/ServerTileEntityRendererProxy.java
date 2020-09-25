package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import net.minecraft.tileentity.TileEntity;

public class ServerTileEntityRendererProxy<T extends TileEntity> implements ITileEntityRendererProxy<T> {
	@Override
	public Function<?, ?> getRenderFactory() {
		return null;
	}

}
