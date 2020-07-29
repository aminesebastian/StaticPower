package theking530.staticpower.fluids;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import theking530.staticpower.fluids.AbstractStaticPowerFluid.Flowing;
import theking530.staticpower.fluids.AbstractStaticPowerFluid.Source;
import theking530.staticpower.items.StaticPowerFluidBucket;
import theking530.staticpower.utilities.Reference;

public class StaticPowerFluidBundle {
	public final String name;
	public final Tag<Fluid> Tag;
	public final StaticPowerFluidBlock FluidBlock;
	public final AbstractStaticPowerFluid.Source Fluid;
	public final AbstractStaticPowerFluid.Flowing FlowingFluid;
	private final Supplier<Item> bucketSupplier;
	private final StaticPowerFluidBuilder builder;
	private Item cachedBucketItem;

	public StaticPowerFluidBundle(String name, Tag<Fluid> tag, StaticPowerFluidBlock fluidBlock, Source fluid, Flowing flowingFluid, Supplier<Item> bucketSupplier, StaticPowerFluidBuilder builder) {
		this.name = name;
		Tag = tag;
		FluidBlock = fluidBlock;
		Fluid = fluid;
		FlowingFluid = flowingFluid;
		this.bucketSupplier = bucketSupplier;
		this.builder = builder;
	}

	public Item getBucket() {
		if (cachedBucketItem == null) {
			cachedBucketItem = bucketSupplier.get();
		}
		return cachedBucketItem;
	}

	public StaticPowerFluidBuilder getSourceBuilder() {
		return builder;
	}

	public static class StaticPowerFluidBuilder {
		public String name;
		private Supplier<Item> bucketSupplier;
		private Consumer<FluidAttributes.Builder> attributes;
		private BucketItem autoBucket;
		private boolean shouldRegisterBucketItem;

		private AbstractStaticPowerFluid.Source fluid;
		private AbstractStaticPowerFluid.Flowing flowingFluid;
		private StaticPowerFluidBlock fluidBlock;

		public StaticPowerFluidBuilder(String name) {
			this.name = name;
			this.shouldRegisterBucketItem = true;
		}

		public StaticPowerFluidBuilder addAttributes(Consumer<FluidAttributes.Builder> attributes) {
			this.attributes = attributes;
			return this;
		}

		public StaticPowerFluidBuilder addAutoBucket() {
			autoBucket = new StaticPowerFluidBucket("bucket_" + name, () -> fluid);
			bucketSupplier = () -> autoBucket;
			return this;
		}

		public StaticPowerFluidBuilder addBucketSupplier(Supplier<Item> bucketSupplier) {
			this.bucketSupplier = bucketSupplier;
			return this;
		}

		public StaticPowerFluidBuilder setShouldRegisterBucket(boolean shouldRegister) {
			shouldRegisterBucketItem = shouldRegister;
			return this;
		}

		public boolean getShouldRegisterBucket() {
			return shouldRegisterBucketItem;
		}

		public StaticPowerFluidBundle build() {
			String stillTexture = "blocks/fluids/" + name + "_still";
			String flowingTexture = "blocks/fluids/" + name + "_flowing";
			Tag<Fluid> tag = new FluidTags.Wrapper(new ResourceLocation(Reference.MOD_ID, name));
			fluidBlock = new StaticPowerFluidBlock(name, () -> fluid, Block.Properties.create(Material.WATER));
			fluid = new AbstractStaticPowerFluid.Source(name, bucketSupplier, () -> fluidBlock, () -> fluid, () -> flowingFluid, stillTexture, flowingTexture, tag, attributes);
			flowingFluid = new AbstractStaticPowerFluid.Flowing(name, bucketSupplier, () -> fluidBlock, () -> fluid, () -> flowingFluid, stillTexture, flowingTexture, tag, attributes);

			return new StaticPowerFluidBundle(name, tag, fluidBlock, fluid, flowingFluid, bucketSupplier, this);

		}
	}
}
