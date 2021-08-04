package theking530.staticpower.blocks.interfaces;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Blocks that implement this interface can define which render layer they
 * belong to.
 * 
 * @author Amine Sebastian
 *
 */
public interface IBlockRenderLayerProvider {
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType();
}
