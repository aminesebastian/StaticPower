package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientTileEntityRendererProxy<T extends TileEntity> implements ITileEntityRendererProxy<T> {
	@OnlyIn(Dist.CLIENT)
	public Function<TileEntityRendererDispatcher, ? extends TileEntityRenderer<T>> rendererFactory;

	public ClientTileEntityRendererProxy(Function<TileEntityRendererDispatcher, ? extends TileEntityRenderer<T>> rendererFactory) {
		this.rendererFactory = rendererFactory;
	}

	@Override
	public Function<?, ?> getRenderFactory() {
		return rendererFactory;
	}

}
