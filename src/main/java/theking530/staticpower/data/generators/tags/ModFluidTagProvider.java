package theking530.staticpower.data.generators.tags;

import org.jetbrains.annotations.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import theking530.staticpower.StaticPower;
import theking530.staticpower.fluid.StaticPowerFluidBundle;
import theking530.staticpower.init.ModFluids;

public class ModFluidTagProvider extends FluidTagsProvider {

	public ModFluidTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
		super(generator, StaticPower.MOD_ID, existingFileHelper);
	}

	@Override
	protected void addTags() {
		for (StaticPowerFluidBundle bundle : ModFluids.FLUID_BUNDLES) {
			tag(bundle.getTag()).add(bundle.getSource().get(), bundle.getFlowing().get());
		}
	}
}
