package theking530.staticpower.data.tiers.lumum;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.StaticPower;
import theking530.staticpower.data.tiers.categories.TierUpgradeConfiguration;

public class LumumUpgradeConfiguration extends TierUpgradeConfiguration {

	public LumumUpgradeConfiguration(Builder builder) {
		super(builder, StaticPower.MOD_ID);
	}

	@Override
	protected int getUpgradeOrdinal() {
		return 5;
	}

	@Override
	protected double getProcessingSpeedUpgrade() {
		return 14.0f;
	}

	@Override
	protected double getProcessingSpeedPowerCost() {
		return 16.0f;
	}

	@Override
	protected double getTankCapacityUpgrade() {
		return 10.0f;
	}

	@Override
	protected double getRangeUpgrade() {
		return 6.0f;
	}

	@Override
	public float getHeatCapacityUpgrade() {
		return 6.0f;
	}

	@Override
	public float getHeatConductivityUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getPowerUpgrade() {
		return 8.0f;
	}

	@Override
	protected double getPowerIoUpgrade() {
		return 16.0f;
	}

	@Override
	protected double getOutputMultiplierUpgrade() {
		return 4.0f;
	}

	@Override
	protected double getOutputMultiplierPowerCostUpgrade() {
		return 2.5f;
	}

	@Override
	protected int getMaxCentrifugeSpeedUpgrade() {
		return 1500;
	}

	@Override
	protected double getCentrifugeUpgradedPowerIncrease() {
		return 6.0f;
	}
}
