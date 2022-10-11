package theking530.staticpower.fluid;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.utilities.SDColor;
import theking530.staticpower.StaticPower;
import theking530.staticpower.init.ModBlocks;
import theking530.staticpower.init.ModFluids;
import theking530.staticpower.init.ModItems;
import theking530.staticpower.init.ModTags;
import theking530.staticpower.items.StaticPowerFluidBucket;

public class StaticPowerFluidBuilder implements IClientFluidTypeExtensions {
	private String name;
	private String textureName;
	private Consumer<FluidType.Properties> extraAttributes;
	private FluidClientExtension clientExtension;
	private AdditionalProperties additionalProperties;

	public RegistryObject<AbstractStaticPowerFluid.Source> source;
	public RegistryObject<AbstractStaticPowerFluid.Flowing> flowing;
	public RegistryObject<StaticPowerFluidBlock> block;
	public RegistryObject<BucketItem> bucket;
	public RegistryObject<FluidType> type;

	public StaticPowerFluidBuilder(String name, SDColor fogColor) {
		this.name = name;
		this.textureName = name;
		this.clientExtension = new FluidClientExtension();
		this.clientExtension.setFogColor(fogColor);
	}

	public StaticPowerFluidBuilder setTint(int tint) {
		clientExtension.setTint(tint);
		return this;
	}

	public StaticPowerFluidBuilder addProperties(Consumer<FluidType.Properties> attributes) {
		this.extraAttributes = attributes;
		return this;
	}

	public StaticPowerFluidBuilder setTextureName(String textureName) {
		this.textureName = textureName;
		return this;
	}

	private ForgeFlowingFluid.Properties getProperties() {
		ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(type, source, flowing);
		properties.block(block);
		properties.bucket(bucket);

		if (additionalProperties != null) {
			properties.explosionResistance(additionalProperties.getExplosionResistance()).levelDecreasePerBlock(additionalProperties.getLevelDecreasePerBlock())
					.slopeFindDistance(additionalProperties.getSlopeFindDistance()).tickRate(additionalProperties.getTickRate());
		}
		return properties;
	}

	public StaticPowerFluidBundle build() {
		TagKey<Fluid> tag = ModTags.createFluidWrapper(new ResourceLocation(StaticPower.MOD_ID, name));
		clientExtension.setFlowingTexture(new ResourceLocation(StaticPower.MOD_ID, "blocks/fluids/" + textureName + "_flowing"));
		clientExtension.setStillTexture(new ResourceLocation(StaticPower.MOD_ID, "blocks/fluids/" + textureName + "_still"));

		FluidType.Properties typeProperties = FluidType.Properties.create();
		if (extraAttributes != null) {
			extraAttributes.accept(typeProperties);
		}

		type = ModFluids.FLUID_TYPES.register(name, () -> new FluidType(typeProperties) {
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(clientExtension);
			}
		});

		source = ModFluids.FLUIDS.register(name, () -> new AbstractStaticPowerFluid.Source(getProperties(), tag));
		flowing = ModFluids.FLUIDS.register(name + "_flowing", () -> new AbstractStaticPowerFluid.Flowing(getProperties(), tag));
		block = ModBlocks.BLOCKS.register(name, () -> new StaticPowerFluidBlock(source, MaterialColor.WATER));
		bucket = ModItems.ITEMS.register("bucket_" + name, () -> new StaticPowerFluidBucket(source));

		return new StaticPowerFluidBundle(tag, type, typeProperties, block, bucket, source, flowing);

	}

	public static class AdditionalProperties {
		private int levelDecreasePerBlock = 1;
		private float explosionResistance = 1;
		private int slopeFindDistance = 4;
		private int tickRate = 5;

		public AdditionalProperties explosionResistance(float resistance) {
			this.explosionResistance = resistance;
			return this;
		}

		public AdditionalProperties levelDecreasePerBlock(int decrease) {
			this.levelDecreasePerBlock = decrease;
			return this;
		}

		public AdditionalProperties slopeFindDistance(int distance) {
			this.slopeFindDistance = distance;
			return this;
		}

		public AdditionalProperties tickRate(int rate) {
			this.tickRate = rate;
			return this;
		}

		public int getLevelDecreasePerBlock() {
			return levelDecreasePerBlock;
		}

		public float getExplosionResistance() {
			return explosionResistance;
		}

		public int getSlopeFindDistance() {
			return slopeFindDistance;
		}

		public int getTickRate() {
			return tickRate;
		}

	}
}
