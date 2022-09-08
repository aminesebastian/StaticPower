package theking530.staticcore.initialization.blockentity;

import java.util.function.Function;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClientBlockEntityRendererProxy<T extends BlockEntity> implements IBlockEntityRendererProxy<T> {
	@OnlyIn(Dist.CLIENT)
	public Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> rendererFactory;

	public ClientBlockEntityRendererProxy(Function<BlockEntityRenderDispatcher, ? extends BlockEntityRenderer<T>> rendererFactory) {
		this.rendererFactory = rendererFactory;
	}

	@Override
	public Function<?, ?> getRenderFactory() {
		return rendererFactory;
	}

}
