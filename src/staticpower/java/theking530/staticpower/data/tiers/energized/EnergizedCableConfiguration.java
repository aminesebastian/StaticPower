package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.cables.TierCableAttachmentConfiguration;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;

public class EnergizedCableConfiguration {
	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableCapillaryFluidTransferRate() {
			return 5;
		}

		@Override
		protected int getCableFluidtransferRate() {
			return 300;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return 1000;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 4;
		}

		@Override
		protected double getItemCableAcceleration() {
			return 1.75f;
		}

		@Override
		protected double getItemCableFriction() {
			return 1.75f;
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
			return 100;
		}

		@Override
		protected double getCablePowerResistance() {
			return 0.01;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.EXTREME;
		}

		@Override
		protected double getWireTerminalMaxCurrent() {
			return 100;
		}

		@Override
		protected int getWireCoilMaxDistance() {
			return 48;
		}

		@Override
		protected StaticPowerVoltage getWireCoilMaxVoltage() {
			return StaticPowerVoltage.EXTREME;
		}

		@Override
		protected double getWireCoilMaxCurrent() {
			return 50;
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 0.1;
		}
	}

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableFilterSlots() {
			return 9;
		}

		@Override
		protected int getCableExtractorRate() {
			return 20;
		}

		@Override
		protected int getCableExtractionStackSize() {
			return 32;
		}

		@Override
		protected int getCableExtractionFluidRate() {
			return 512;
		}

		@Override
		protected int getCableExtractionFilterSlots() {
			return 9;
		}

		@Override
		protected double getExtractedItemInitialSpeed() {
			return 2;
		}

		@Override
		protected int getCableRetrievalRate() {
			return 20;
		}

		@Override
		protected int getCableRetrievalStackSize() {
			return 32;
		}

		@Override
		protected int getCableRetrievalFilterSlots() {
			return 4;
		}

		@Override
		protected double getRetrievedItemInitialSpeed() {
			return 2;
		}
	}
}
