package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticcore.data.tier.cables.TierCableAttachmentConfiguration;
import theking530.staticcore.data.tier.cables.TierFluidCableConfiguration;
import theking530.staticcore.data.tier.cables.TierItemCableConfiguration;
import theking530.staticcore.data.tier.cables.TierPowerCableConfiguration;

public class BasicCableConfiguration {
	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableFluidtransferRate() {
			return 50;
		}

		@Override
		protected int getCableCapillaryFluidTransferRate() {
			return 1;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return 125;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 1;
		}

		@Override
		protected double getItemCableAcceleration() {
			return 1.1f;
		}

		@Override
		protected double getItemCableFriction() {
			return 2.5f;
		}
	}

	public static class PowerCableConfiguration extends TierPowerCableConfiguration {
		public PowerCableConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.LOW;
		}

		@Override
		protected double getCableMaxCurrent() {
			return 5;
		}

		@Override
		protected double getCablePowerResistance() {
			return 0.1;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.LOW;
		}

		@Override
		protected double getWireTerminalMaxCurrent() {
			return 5;
		}

		@Override
		protected int getWireCoilMaxDistance() {
			return 12;
		}

		@Override
		protected StaticPowerVoltage getWireCoilMaxVoltage() {
			return StaticPowerVoltage.LOW;
		}

		@Override
		protected double getWireCoilMaxCurrent() {
			return 5;
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 1;
		}
	}

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder, String modId) {
			super(builder, modId);
		}

		@Override
		protected int getCableFilterSlots() {
			return 3;
		}

		@Override
		protected int getCableExtractorRate() {
			return 40;
		}

		@Override
		protected int getCableExtractionStackSize() {
			return 4;
		}

		@Override
		protected int getCableExtractionFluidRate() {
			return 32;
		}

		@Override
		protected int getCableExtractionFilterSlots() {
			return 3;
		}

		@Override
		protected double getExtractedItemInitialSpeed() {
			return .2;
		}

		@Override
		protected int getCableRetrievalRate() {
			return 40;
		}

		@Override
		protected int getCableRetrievalStackSize() {
			return 4;
		}

		@Override
		protected int getCableRetrievalFilterSlots() {
			return 1;
		}

		@Override
		protected double getRetrievedItemInitialSpeed() {
			return .2;
		}
	}
}
