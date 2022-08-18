package theking530.staticpower.fluid;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.blocks.interfaces.IRenderLayerProvider;

public class StaticPowerFluidBlock extends LiquidBlock implements IRenderLayerProvider {

	public StaticPowerFluidBlock(RegistryObject<AbstractStaticPowerFluid.Source> fluid, Properties properties) {
		super(fluid, properties.noCollission().noDrops());
	}

	public StaticPowerFluidBlock(RegistryObject<FlowingFluid> fluid, MaterialColor Color) {
		super(fluid, BlockBehaviour.Properties.of(Material.WATER, Color).noCollission().noDrops());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.translucent();
	}
}
