package theking530.staticcore.data.tiers.creative;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.api.energy.utilities.StaticPowerEnergyUtilities;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;

public class CreativeCableConfiguration {

	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableCapillaryFluidTransferRate() {
			return Integer.MAX_VALUE;
		}

		@Override
		protected int getCableFluidtransferRate() {
			return Integer.MAX_VALUE;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return Integer.MAX_VALUE;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
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
		public PowerCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.EXTREME;
		}

		@Override
		protected double getCableMaxCurrent() {
			return StaticPowerEnergyUtilities.getMaximumPower();
		}

		@Override
		protected double getCablePowerResistance() {
			return 0;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.EXTREME;
		}

		@Override
		protected int getWireCoilMaxDistance() {
			return 64;
		}

		@Override
		protected double getWireTerminalMaxCurrent() {
			return StaticPowerEnergyUtilities.getMaximumPower();
		}

		@Override
		protected StaticPowerVoltage getWireCoilMaxVoltage() {
			return StaticPowerVoltage.EXTREME;
		}

		@Override
		protected double getWireCoilMaxCurrent() {
			return StaticPowerEnergyUtilities.getMaximumPower();
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 0;
		}
	}
}
