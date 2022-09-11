package theking530.staticpower.data.tiers.energized;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import theking530.staticpower.data.tiers.categories.cables.TierCableAttachmentConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierFluidCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierItemCableConfiguration;
import theking530.staticpower.data.tiers.categories.cables.TierPowerCableConfiguration;

public class EnergizedCableConfiguration {
	public static class FluidCableConfiguration extends TierFluidCableConfiguration {
		public FluidCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected int getCableFluidCapacity() {
			return 2500;
		}

		@Override
		protected int getCableIndustrialFluidCapacity() {
			return 25000;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return 2;
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
		public PowerCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getCablePowerMaxCurrent() {
			return 35;
		}

		@Override
		protected double getCablePowerResistancePerBlock() {
			return 0.5;
		}

		@Override
		protected double getCableIndustrialPowerMaxCurrent() {
			return 400;
		}

		@Override
		protected double getCableIndustrialPowerResistancePerBlock() {
			return 5;
		}
	}

	public static class CableAttachmentConfiguration extends TierCableAttachmentConfiguration {
		public CableAttachmentConfiguration(Builder builder) {
			super(builder);
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
