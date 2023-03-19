package theking530.staticpower.data.generators.tags;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticcore.fluid.StaticPowerFluidBundle;
import theking530.staticcore.utilities.MinecraftColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.tags.ModFluidTags;

public class ModFluidTagProvider extends FluidTagsProvider {

	public ModFluidTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		for (StaticPowerFluidBundle bundle : ModFluids.FLUID_BUNDLES) {
			tag(bundle.getTag()).add(bundle.getSource().get(), bundle.getFlowing().get());
		}

		for (MinecraftColor color : MinecraftColor.values()) {
			tag(ModFluidTags.CONCRETE).add(ModFluids.CONCRETE.get(color).getSource().get(), ModFluids.CONCRETE.get(color).getFlowing().get());
			tag(ModFluidTags.DYE).add(ModFluids.DYES.get(color).getSource().get(), ModFluids.DYES.get(color).getFlowing().get());
		}
	}
}
