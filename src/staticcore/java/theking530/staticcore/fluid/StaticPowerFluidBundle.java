package theking530.staticcore.fluid;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.FluidType.Properties;
import net.minecraftforge.registries.RegistryObject;
import theking530.staticcore.fluid.AbstractStaticPowerFluid.Flowing;
import theking530.staticcore.fluid.AbstractStaticPowerFluid.Source;

public class StaticPowerFluidBundle {
	protected final TagKey<Fluid> tag;
	protected final RegistryObject<StaticCoreFluidType> type;
	protected final FluidType.Properties typeProperties;
	protected final RegistryObject<StaticPowerFluidBlock> block;
	protected final RegistryObject<BucketItem> bucket;
	protected final RegistryObject<AbstractStaticPowerFluid.Source> source;
	protected final RegistryObject<AbstractStaticPowerFluid.Flowing> flowing;

	public StaticPowerFluidBundle(TagKey<Fluid> tag, RegistryObject<StaticCoreFluidType> type, Properties typeProperties, RegistryObject<StaticPowerFluidBlock> block,
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

	public RegistryObject<StaticCoreFluidType> getType() {
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
