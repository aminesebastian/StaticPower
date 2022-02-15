package theking530.staticpower.fluid;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import theking530.staticpower.fluid.AbstractStaticPowerFluid.Flowing;
import theking530.staticpower.fluid.AbstractStaticPowerFluid.Source;
import theking530.staticpower.items.StaticPowerFluidBucket;

public class StaticPowerFluidBundle {
	public final String name;
	public final Named<Fluid> Tag;
	public final StaticPowerFluidBlock FluidBlock;
	public final AbstractStaticPowerFluid.Source Fluid;
	public final AbstractStaticPowerFluid.Flowing FlowingFluid;
	private final Supplier<Item> bucketSupplier;
	private final StaticPowerFluidBuilder builder;
	private Item cachedBucketItem;

	public StaticPowerFluidBundle(String name, Named<Fluid> tag, StaticPowerFluidBlock fluidBlock, Source fluid, Flowing flowingFluid, Supplier<Item> bucketSupplier, StaticPowerFluidBuilder builder) {
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
		private String textureName;
		private Supplier<Item> bucketSupplier;
		private Consumer<FluidAttributes.Builder> attributes;
		private StaticPowerFluidBucket autoBucket;
		private boolean shouldRegisterBucketItem;

		private AbstractStaticPowerFluid.Source fluid;
		private AbstractStaticPowerFluid.Flowing flowingFluid;
		private StaticPowerFluidBlock fluidBlock;

		public StaticPowerFluidBuilder(String name) {
			this.name = name;
			this.textureName = name;
			this.shouldRegisterBucketItem = true;
		}

		public StaticPowerFluidBuilder addAttributes(Consumer<FluidAttributes.Builder> attributes) {
			this.attributes = attributes;
			return this;
		}

		public StaticPowerFluidBuilder addAutoBucket(boolean dynamicModel, ResourceLocation bucketMask) {
			autoBucket = new StaticPowerFluidBucket(dynamicModel, bucketMask, "bucket_" + name, () -> fluid);
			bucketSupplier = () -> autoBucket;
			shouldRegisterBucketItem = true;
			return this;
		}

		public StaticPowerFluidBuilder addAutoBucket() {
			return addAutoBucket(false, null);
		}

		public StaticPowerFluidBuilder addBucketSupplier(Supplier<Item> bucketSupplier) {
			this.bucketSupplier = bucketSupplier;
			return this;
		}

		public StaticPowerFluidBuilder setTextureName(String textureName) {
			this.textureName = textureName;
			return this;
		}

		public boolean getShouldRegisterBucket() {
			return shouldRegisterBucketItem;
		}

		public StaticPowerFluidBundle build() {
			String stillTexture = "blocks/fluids/" + textureName + "_still";
			String flowingTexture = "blocks/fluids/" + textureName + "_flowing";
			Named<Fluid> tag = FluidTags.bind(name);
			fluidBlock = new StaticPowerFluidBlock(name, () -> fluid, Block.Properties.of(Material.WATER));
			fluid = new AbstractStaticPowerFluid.Source(name, bucketSupplier, () -> fluidBlock, () -> fluid, () -> flowingFluid, stillTexture, flowingTexture, tag, attributes);
			flowingFluid = new AbstractStaticPowerFluid.Flowing(name, bucketSupplier, () -> fluidBlock, () -> fluid, () -> flowingFluid, stillTexture, flowingTexture, tag, attributes);

			return new StaticPowerFluidBundle(name, tag, fluidBlock, fluid, flowingFluid, bucketSupplier, this);

		}
	}
}
