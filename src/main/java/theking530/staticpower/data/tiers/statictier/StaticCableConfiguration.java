package theking530.staticpower.data.tiers.statictier;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class StaticCableConfiguration {
	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFluidtransferRate() {
			return 150;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return 500;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 3.0;
		}

		@Override
		protected double getItemCableAcceleration() {
			return 1.4f;
		}

		@Override
		protected double getItemCableFriction() {
			return 2.0f;
		}
	}

	public static class PowerCableConfiguration extends TierPowerCableConfiguration {
		public PowerCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.HIGH;
		}

		@Override
		protected double getCableMaxCurrent() {
			return 20;
		}

		@Override
		protected double getCablePowerLossPerBlock() {
			return 0.25;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.HIGH;
		}

		@Override
		protected double getWireTerminalMaxCurrent() {
			return 20;
		}

		@Override
		protected int getWireCoilMaxDistance() {
			return 32;
		}

		@Override
		protected StaticPowerVoltage getWireCoilMaxVoltage() {
			return StaticPowerVoltage.HIGH;
		}

		@Override
		protected double getWireCoilMaxCurrent() {
			return 20;
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 0.25;
		}
	}

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFilterSlots() {
			return 7;
		}

		@Override
		protected int getCableExtractorRate() {
			return 20;
		}

		@Override
		protected int getCableExtractionStackSize() {
			return 16;
		}

		@Override
		protected int getCableExtractionFluidRate() {
			return 256;
		}

		@Override
		protected int getCableExtractionFilterSlots() {
			return 7;
		}

		@Override
		protected double getExtractedItemInitialSpeed() {
			return 1.0;
		}

		@Override
		protected int getCableRetrievalRate() {
			return 20;
		}

		@Override
		protected int getCableRetrievalStackSize() {
			return 16;
		}

		@Override
		protected int getCableRetrievalFilterSlots() {
			return 3;
		}

		@Override
		protected double getRetrievedItemInitialSpeed() {
			return 1.0;
		}
	}
}
