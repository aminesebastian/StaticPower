package theking530.staticcore.initialization.tileentity;

import java.util.function.Function;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientTileEntityRendererProxy<T extends BlockEntity> implements ITileEntityRendererProxy<T> {
	@OnlyIn(Dist.CLIENT)
	public Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> rendererFactory;

	public ClientTileEntityRendererProxy(Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> rendererFactory) {
		this.rendererFactory = rendererFactory;
	}

	@Override
	public Function<?, ?> getRenderFactory() {
		return rendererFactory;
	}

}
