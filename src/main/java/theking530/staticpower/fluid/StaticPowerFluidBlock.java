package theking530.staticpower.fluid;

import java.util.function.Supplier;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.fluid.FlowingFluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;

public class StaticPowerFluidBlock extends FlowingFluidBlock implements IRenderLayerProvider {

	public StaticPowerFluidBlock(String name, Supplier<FlowingFluid> fluid, Properties properties) {
		super(fluid, properties.doesNotBlockMovement().noDrops());
		setRegistryName(name);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.getTranslucent();
	}
}
