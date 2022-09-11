package theking530.staticpower.data.tiers.basic;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
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
		protected int getCableFluidCapacity() {
			return 100;
		}

		@Override
		protected int getCableIndustrialFluidCapacity() {
			return 1000;
		}
	}

	public static class ItemCableConfiguration extends TierItemCableConfiguration {
		public ItemCableConfiguration(Builder builder) {
			super(builder);
		}

		@Override
		protected double getItemCableMaxSpeed() {
			return .4;
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
		protected double getCablePowerMaxCurrent() {
			return 10;
		}

		@Override
		protected double getCablePowerResistancePerBlock() {
			return 10;
		}

		@Override
		protected double getCableIndustrialPowerMaxCurrent() {
			return 100;
		}

		@Override
		protected double getCableIndustrialPowerResistancePerBlock() {
			return 100;
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