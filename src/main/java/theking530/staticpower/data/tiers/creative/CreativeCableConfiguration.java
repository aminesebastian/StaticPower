package theking530.staticpower.data.tiers.creative;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class CreativeCableConfiguration {

	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFluidCapacity() {
			return Integer.MAX_VALUE;
		}

		@Override
		protected int getCableIndustrialFluidCapacity() {
			return Integer.MAX_VALUE;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 10;
		}

		@Override
		protected double getItemCableAcceleration() {
			return 1.0f;
		}

		@Override
		protected double getItemCableFriction() {
			return 0;
		}
	}

	public static class PowerCableConfiguration extends TierPowerCableConfiguration {
		public PowerCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.EXTREME;
		}

		@Override
		protected double getCableMaxPower() {
			return Double.MAX_VALUE;
		}

		@Override
		protected double getCablePowerLossPerBlock() {
			return 0;
		}
	}
}
