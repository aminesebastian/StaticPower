package theking530.staticpower.blocks;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Basic class for a decorative cut-out block.
 * 
 * @author Amine Sebastian
 *
 */
public class StaticPowerCutoutBlock extends StaticPowerBlock {
	public StaticPowerCutoutBlock(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}
}
