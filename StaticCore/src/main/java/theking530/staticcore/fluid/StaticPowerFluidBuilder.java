package theking530.staticcore.fluid;

import java.util.function.Consumer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.item.StaticCoreFluidBucket;
import theking530.staticcore.utilities.SDColor;

public class StaticPowerFluidBuilder implements IClientFluidTypeExtensions {
	private final String name;
	private final String modId;
	private final CreativeModeTab creativeTab;

	private String textureName;
	private Consumer<FluidType.Properties> extraAttributes;
	private FluidClientExtension clientExtension;
	private AdditionalProperties additionalProperties;

	public RegistryObject<AbstractStaticPowerFluid.Source> source;
	public RegistryObject<AbstractStaticPowerFluid.Flowing> flowing;
	public RegistryObject<StaticPowerFluidBlock> block;
	public RegistryObject<BucketItem> bucket;
	public RegistryObject<FluidType> type;

	public StaticPowerFluidBuilder(String modId, CreativeModeTab creativeTab, String name, SDColor fogColor) {
		this.name = name;
		this.modId = modId;
		this.creativeTab = creativeTab;
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

	public StaticPowerFluidBundle build(DeferredRegister<FluidType> fluidTypeRegistry, DeferredRegister<Fluid> fluidRegistry, DeferredRegister<Block> blockRegistry,
			DeferredRegister<Item> itemRegistry) {
		TagKey<Fluid> tag = TagKey.create(Registry.FLUID_REGISTRY, new ResourceLocation(modId, name));
		clientExtension.setFlowingTexture(new ResourceLocation(modId, "blocks/fluids/" + textureName + "_flowing"));
		clientExtension.setStillTexture(new ResourceLocation(modId, "blocks/fluids/" + textureName + "_still"));

		FluidType.Properties typeProperties = FluidType.Properties.create();
		if (extraAttributes != null) {
			extraAttributes.accept(typeProperties);
		}

		type = fluidTypeRegistry.register(name, () -> new FluidType(typeProperties) {
			@Override
			public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
				consumer.accept(clientExtension);
			}
		});

		source = fluidRegistry.register(name, () -> new AbstractStaticPowerFluid.Source(getProperties(), tag));
		flowing = fluidRegistry.register(name + "_flowing", () -> new AbstractStaticPowerFluid.Flowing(getProperties(), tag));
		block = blockRegistry.register(name, () -> new StaticPowerFluidBlock(source, MaterialColor.WATER));
		bucket = itemRegistry.register("bucket_" + name, () -> new StaticCoreFluidBucket(creativeTab, source));

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
