package theking530.staticpower.blockentities.components;

import net.minecraft.resources.ResourceLocation;
import theking530.api.energy.CurrentType;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.StaticCoreConfig;
import theking530.staticcore.blockentity.components.energy.PowerStorageComponent;
import theking530.staticpower.data.StaticPowerTier;

public class TieredPowerStorageComponent extends PowerStorageComponent {
	
	public TieredPowerStorageComponent(String name, ResourceLocation staticPowerTier, boolean canAcceptExternalPower, boolean canOutputExternalPower) {
		super(name, 0, StaticPowerVoltage.ZERO, StaticPowerVoltage.ZERO, 0, new CurrentType[] { CurrentType.DIRECT }, StaticPowerVoltage.ZERO, 0, CurrentType.DIRECT, canAcceptExternalPower,
				canOutputExternalPower);

		// Get the tier.
		StaticPowerTier tierObject = (StaticPowerTier)StaticCoreConfig.getTier(staticPowerTier);
		setCapacity(tierObject.powerConfiguration.defaultPowerCapacity.get());
		setInputVoltageRange(tierObject.powerConfiguration.getDefaultInputVoltageRange().copy());
		setMaximumInputPower(tierObject.powerConfiguration.defaultMaximumPowerInput.get());
		setMaximumOutputPower(tierObject.powerConfiguration.defaultMaximumPowerOutput.get());
		setOutputVoltage(tierObject.powerConfiguration.defaultOutputVoltage.get());
	}
}
