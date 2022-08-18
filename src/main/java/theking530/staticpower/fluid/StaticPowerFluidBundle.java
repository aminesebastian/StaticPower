package theking530.staticpower.fluid;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.utilities.Color;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.StaticPowerFluidBucket;

public class StaticPowerFluidBundle {
	public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation("block/water_overlay");

	public final TagKey<Fluid> tag;
	public final RegistryObject<StaticPowerFluidBlock> block;
	public final RegistryObject<AbstractStaticPowerFluid.Source> source;
	public final RegistryObject<AbstractStaticPowerFluid.Flowing> flowing;
	private final RegistryObject<Item> bucket;

	public StaticPowerFluidBundle(TagKey<Fluid> tag, RegistryObject<StaticPowerFluidBlock> block, RegistryObject<AbstractStaticPowerFluid.Source> source,
			RegistryObject<AbstractStaticPowerFluid.Flowing> flowing, RegistryObject<Item> bucket) {
		this.tag = tag;
		this.block = block;
		this.source = source;
		this.flowing = flowing;
		this.bucket = bucket;
	}

	public Item getBucket() {
		return bucket.get();
	}

	public static class StaticPowerFluidBuilder {
		public String name;
		private String textureName;
		private Consumer<FluidAttributes.Builder> extraAttributes;
		private boolean shouldRegisterBucketItem;

		private Color fogColor;
		private Color overlayColor;

		private ForgeFlowingFluid.Properties properties;
		private RegistryObject<AbstractStaticPowerFluid.Source> source;
		private RegistryObject<AbstractStaticPowerFluid.Flowing> flowing;
		private RegistryObject<StaticPowerFluidBlock> block;
		private RegistryObject<Item> bucket;

		public StaticPowerFluidBuilder(String name, Color color) {
			this.name = name;
			this.textureName = name;
			this.shouldRegisterBucketItem = true;
			this.setFogColor(color);
		}

		public StaticPowerFluidBuilder addAttributes(Consumer<FluidAttributes.Builder> attributes) {
			this.extraAttributes = attributes;
			return this;
		}

		public StaticPowerFluidBuilder addAutoBucket(boolean dynamicModel, ResourceLocation bucketMask) {
			bucket = ModItems.ITEMS.register("bucket_" + name, () -> new StaticPowerFluidBucket(dynamicModel, bucketMask, source));
			shouldRegisterBucketItem = true;
			return this;
		}

		public StaticPowerFluidBuilder addAutoBucket() {
			return addAutoBucket(false, null);
		}

		public StaticPowerFluidBuilder setFogColor(Color color) {
			fogColor = color.copy();
			if (overlayColor == null) {
				overlayColor = fogColor;
			}
			return this;
		}

		public StaticPowerFluidBuilder setOverlayColor(Color color) {
			overlayColor = color.copy();
			if (fogColor == null) {
				fogColor = overlayColor;
			}
			return this;
		}

		public StaticPowerFluidBuilder setTextureName(String textureName) {
			this.textureName = textureName;
			return this;
		}

		public StaticPowerFluidBundle build() {
			String stillTexture = "blocks/fluids/" + textureName + "_still";
			String flowingTexture = "blocks/fluids/" + textureName + "_flowing";
			TagKey<Fluid> tag = ModTags.createFluidWrapper(new ResourceLocation(StaticPower.MOD_ID, name));

			source = ModFluids.FLUIDS.register(name, () -> new AbstractStaticPowerFluid.Source(properties, tag, fogColor, overlayColor));
			flowing = ModFluids.FLUIDS.register(name + "_flowing", () -> new AbstractStaticPowerFluid.Flowing(properties, tag, fogColor, overlayColor));
			block = ModBlocks.BLOCKS.register(name, () -> new StaticPowerFluidBlock(source, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100f).noDrops()));

			FluidAttributes.Builder attrbiuteBuilder = FluidAttributes
					.builder(new ResourceLocation(StaticPower.MOD_ID, stillTexture), new ResourceLocation(StaticPower.MOD_ID, flowingTexture)).overlay(WATER_OVERLAY_RL);
			if (extraAttributes != null) {
				extraAttributes.accept(attrbiuteBuilder);
			}

			properties = new ForgeFlowingFluid.Properties(() -> source.get(), () -> flowing.get(), attrbiuteBuilder).slopeFindDistance(2).levelDecreasePerBlock(2)
					.block(() -> block.get()).bucket(() -> bucket.get());

			return new StaticPowerFluidBundle(tag, block, source, flowing, bucket);

		}
	}
}
