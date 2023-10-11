package theking530.staticpower.data.tiers.advanced;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.cables.TierCableAttachmentConfiguration;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;

public class AdvancedCableConfiguration {

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableExtractorRate() {
			return 40;
		}

		@Override
		protected int getCableExtractionStackSize() {
			return 8;
		}

		@Override
		protected int getCableExtractionFluidRate() {
			return 64;
		}

		@Override
		protected int getCableExtractionFilterSlots() {
			return 5;
		}

		@Override
		protected double getExtractedItemInitialSpeed() {
			return 0.5;
		}

		@Override
		protected int getCableRetrievalRate() {
			return 40;
		}

		@Override
		protected int getCableRetrievalStackSize() {
			return 8;
		}

		@Override
		protected int getCableRetrievalFilterSlots() {
			return 2;
		}

		@Override
		protected double getRetrievedItemInitialSpeed() {
			return 0.5;
		}

		@Override
		protected int getCableFilterSlots() {
			return 5;
		}
	}

	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableCapillaryFluidTransferRate() {
			return 2;
		}

		@Override
		protected int getCableFluidtransferRate() {
			return 100;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return 250;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 2.0;
		}

		@Override
		protected double getItemCableAcceleration() {
			return 1.25f;
		}

		@Override
		protected double getItemCableFriction() {
			return 2.25f;
		}
	}

	public static class PowerCableConfiguration extends TierPowerCableConfiguration {
		public PowerCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.MEDIUM;
		}

		@Override
		protected double getCableMaxCurrent() {
			return 100;
		}

		@Override
		protected double getCablePowerResistance() {
			return 0.050;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.MEDIUM;
		}

		@Override
		protected double getWireTerminalMaxCurrent() {
			return 100;
		}

		@Override
		protected int getWireCoilMaxDistance() {
			return 24;
		}

		@Override
		protected StaticPowerVoltage getWireCoilMaxVoltage() {
			return StaticPowerVoltage.MEDIUM;
		}

		@Override
		protected double getWireCoilMaxCurrent() {
			return 10;
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 0.5;
		}
	}
}
