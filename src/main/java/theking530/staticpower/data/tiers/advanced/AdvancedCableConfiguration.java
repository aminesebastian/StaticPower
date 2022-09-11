package theking530.staticpower.data.tiers.advanced;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class AdvancedCableConfiguration {

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder) {
			super(builder);
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
		public FluidCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFluidCapacity() {
			return 250;
		}

		@Override
		protected int getCableIndustrialFluidCapacity() {
			return 2500;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 1.0;
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
		public PowerCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getCablePowerMaxCurrent() {
			return 15;
		}

		@Override
		protected double getCablePowerResistancePerBlock() {
			return 5;
		}

		@Override
		protected double getCableIndustrialPowerMaxCurrent() {
			return 200;
		}

		@Override
		protected double getCableIndustrialPowerResistancePerBlock() {
			return 50;
		}
	}
}