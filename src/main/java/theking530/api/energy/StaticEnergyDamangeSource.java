package theking530.api.energy;

import net.minecraft.world.damagesource.DamageSource;
import theking530.staticpower.StaticPower;

public class StaticEnergyDamangeSource extends DamageSource {

	private static StaticEnergyDamangeSource INSTANCE;

	private StaticEnergyDamangeSource(String p_19333_) {
		super(p_19333_);
	}

	public static StaticEnergyDamangeSource get() {
		if (INSTANCE == null) {
			INSTANCE = new StaticEnergyDamangeSource(StaticPower.MOD_ID + "_electrocution");
			INSTANCE.bypassArmor().bypassMagic();
		}
		return INSTANCE;
	}
}
