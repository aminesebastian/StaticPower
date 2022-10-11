package theking530.staticpower.world.fluid;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModFluids;

public class OilLakeRegistration extends ModLakePlacement<OilLakeRegistration> {

	public OilLakeRegistration() {
		super("oil_deposit");
	}

	@Override
	public StaticPowerFluidBundle getFluidBundle() {
		return ModFluids.CrudeOil;
	}

	@Override
	public BlockStateProvider getBarrier() {
		return BlockStateProvider.simple(Blocks.BASALT);
	}

	@Override
	public IntProvider getRadius() {
		return UniformInt.of(5, 20);
	}

	@Override
	public IntProvider getDepth() {
		return UniformInt.of(10, 100);
	}

	@Override
	public int getRarity() {
		return 200;
	}
}
