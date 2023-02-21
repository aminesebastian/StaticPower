package theking530.staticpower.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidType.Properties;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticpower.StaticPower;
import theking530.staticpower.fluid.AbstractStaticPowerFluid.Flowing;
import theking530.staticpower.fluid.AbstractStaticPowerFluid.Source;

public class StaticPowerFluidBundle {
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(StaticPower.MOD_ID, "textures/misc/underfluid.png");

	protected final TagKey<Fluid> tag;
	protected final RegistryObject<FluidType> type;
	protected final FluidType.Properties typeProperties;
	protected final RegistryObject<StaticPowerFluidBlock> block;
	protected final RegistryObject<BucketItem> bucket;
	protected final RegistryObject<AbstractStaticPowerFluid.Source> source;
	protected final RegistryObject<AbstractStaticPowerFluid.Flowing> flowing;

	public StaticPowerFluidBundle(TagKey<Fluid> tag, RegistryObject<FluidType> type, Properties typeProperties, RegistryObject<StaticPowerFluidBlock> block,
			RegistryObject<BucketItem> bucket, RegistryObject<Source> source, RegistryObject<Flowing> flowing) {
		this.tag = tag;
		this.type = type;
		this.typeProperties = typeProperties;
		this.block = block;
		this.bucket = bucket;
		this.source = source;
		this.flowing = flowing;
	}

	public Item getBucket() {
		return bucket.get();
	}

	public TagKey<Fluid> getTag() {
		return tag;
	}

	public RegistryObject<FluidType> getType() {
		return type;
	}

	public FluidType.Properties getTypeProperties() {
		return typeProperties;
	}

	public RegistryObject<StaticPowerFluidBlock> getBlock() {
		return block;
	}

	public RegistryObject<AbstractStaticPowerFluid.Source> getSource() {
		return source;
	}

	public RegistryObject<AbstractStaticPowerFluid.Flowing> getFlowing() {
		return flowing;
	}
}
