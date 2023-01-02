package theking530.staticpower.data.tiers.lumum;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class LumumCableConfiguration {
	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFluidtransferRate() {
			return 600;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return 2000;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 5;
		}

		@Override
		protected double getItemCableAcceleration() {
			return 2.0f;
		}

		@Override
		protected double getItemCableFriction() {
			return 1.5f;
		}
	}

	public static class PowerCableConfiguration extends TierPowerCableConfiguration {
		public PowerCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.BONKERS;
		}

		@Override
		protected double getCableMaxPower() {
			return 320;
		}

		@Override
		protected double getCablePowerLossPerBlock() {
			return 0.05;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.BONKERS;
		}

		@Override
		protected double getWireTerminalMaxPower() {
			return 1000;
		}

		@Override
		protected int getWireCoilMaxDistance() {
			return 64;
		}

		@Override
		protected StaticPowerVoltage getWireCoilMaxVoltage() {
			return StaticPowerVoltage.BONKERS;
		}

		@Override
		protected double getWireCoilMaxPower() {
			return 1280;
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 0.05;
		}
	}

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFilterSlots() {
			return 12;
		}

		@Override
		protected int getCableExtractorRate() {
			return 20;
		}

		@Override
		protected int getCableExtractionStackSize() {
			return 64;
		}

		@Override
		protected int getCableExtractionFluidRate() {
			return 1024;
		}

		@Override
		protected int getCableExtractionFilterSlots() {
			return 12;
		}

		@Override
		protected double getExtractedItemInitialSpeed() {
			return 2;
		}

		@Override
		protected int getCableRetrievalRate() {
			return 2;
		}

		@Override
		protected int getCableRetrievalStackSize() {
			return 64;
		}

		@Override
		protected int getCableRetrievalFilterSlots() {
			return 5;
		}

		@Override
		protected double getRetrievedItemInitialSpeed() {
			return 2;
		}

	}
}
