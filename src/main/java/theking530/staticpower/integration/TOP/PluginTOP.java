package theking530.staticpower.integration.TOP;

import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.InterModComms;
import theking530.staticpower.StaticPower;
import theking530.staticpower.integration.ICompatibilityPlugin;

public class PluginTOP implements ICompatibilityPlugin {

	@Override
	public void register() {
		Supplier<Function<ITheOneProbe, Void>> supplier = () -> this::registerProviders;
		InterModComms.sendTo("theoneprobe", "getTheOneProbe", supplier);

	}

	@Nullable
	public Void registerProviders(@Nullable ITheOneProbe input) {
		Preconditions.checkNotNull(input);

		input.registerProvider(new StaticEnergyInfoProvider());
		input.registerProvider(new ProgressInfoProvider());
		input.registerProvider(new HeatInfoProvider());
		input.registerProvider(new DigistoreInfoProvider());

		return null;
	}

	@Override
	public ResourceLocation getPluginName() {
		return new ResourceLocation(StaticPower.MOD_ID, "theoneprobe");
	}
}
