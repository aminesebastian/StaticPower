package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.api.energy.StaticPowerVoltage;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class BasicCableConfiguration {
	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFluidtransferRate() {
			return 50;
		}

		@Override
		protected int getCableIndustrialFluidTransferRate() {
			return 125;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
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
		public PowerCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected StaticPowerVoltage getCableMaxVoltage() {
			return StaticPowerVoltage.LOW;
		}

		@Override
		protected double getCableMaxPower() {
			return 20;
		}

		@Override
		protected double getCablePowerLossPerBlock() {
			return 1;
		}

		@Override
		protected StaticPowerVoltage getWireTerminalMaxVoltage() {
			return StaticPowerVoltage.LOW;
		}

		@Override
		protected double getWireTerminalMaxPower() {
			return 60;
		}

		@Override
		protected double getWireCoilPowerLossPerBlock() {
			return 1;
		}
	}

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder) {
			super(builder);
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
