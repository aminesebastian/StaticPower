package theking530.staticpower.fluid;

import java.util.function.Supplier;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class StaticPowerFluidBlock extends LiquidBlock implements IRenderLayerProvider {

	public StaticPowerFluidBlock(String name, Supplier<FlowingFluid> fluid, Properties properties) {
		super(fluid, properties.noCollission().noDrops());
		setRegistryName(name);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.translucent();
	}
}
